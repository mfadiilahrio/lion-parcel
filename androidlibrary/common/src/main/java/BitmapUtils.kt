import android.content.Context
import android.graphics.*
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class BitmapUtils {
    companion object
}

@Throws(IOException::class)
fun BitmapUtils.Companion.compressImage(
    context: Context,
    uri: Uri,
    reqWidth: Float = 1000.0f,
    reqHeight: Float = 1000.0f,
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 100,
    destinationPath: String
): File {

    var fileOutputStream: FileOutputStream? = null

    val file = File(destinationPath).parentFile ?: throw Throwable("File is empty")

    if (!file.exists()) {
        file.mkdirs()
    }

    val inputStream =
        context.contentResolver.openInputStream(uri) ?: throw Throwable("Input stream is empty")

    try {

        fileOutputStream = FileOutputStream(destinationPath)

        // write the compressed bitmap at the destination specified by destinationPath.
        val bitmap = decodeSampledBitmapFromInputStream(
            context,
            uri,
            inputStream,
            reqWidth,
            reqHeight
        )

        bitmap?.compress(
            compressFormat,
            quality,
            fileOutputStream
        )

    } finally {
        if (fileOutputStream != null) {
            fileOutputStream.flush()
            fileOutputStream.close()
        }
    }

    inputStream.close()

    return File(destinationPath)
}

@Throws(IOException::class)
private fun decodeSampledBitmapFromInputStream(
    context: Context,
    uri: Uri,
    inputStream: InputStream,
    reqWidth: Float,
    reqHeight: Float
): Bitmap? {

    // Using input stream directly to get orientation will result null bitmap when doing decodeByteArray
    val orientationStream =
        context.contentResolver.openInputStream(uri) ?: throw Throwable("Input stream is empty")
    val orientation = getOrientation(orientationStream)

    orientationStream.close()

    val byteArray = inputStream.readBytes()

    var scaledBitmap: Bitmap? = null
    var bmp: Bitmap?

    val options = BitmapFactory.Options()

    // First decode with inJustDecodeBounds=true to check dimensions
    options.inJustDecodeBounds = true

    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)

    var actualHeight = options.outHeight
    var actualWidth = options.outWidth

    var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
    val maxRatio = reqWidth / reqHeight

    if (actualHeight > reqHeight || actualWidth > reqWidth) {
        //If Height is greater
        when {
            imgRatio < maxRatio -> {
                imgRatio = reqHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = reqHeight.toInt()

            }  //If Width is greater
            imgRatio > maxRatio -> {
                imgRatio = reqWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = reqWidth.toInt()
            }
            else -> {
                actualHeight = reqHeight.toInt()
                actualWidth = reqWidth.toInt()
            }
        }
    }

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(
        options,
        actualWidth,
        actualHeight
    )
    options.inJustDecodeBounds = false
    options.inTempStorage = ByteArray(16 * 1024)

    try {
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }

    if (bmp == null) {
        return null
    }

    try {
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }

    if (scaledBitmap == null) {
        return null
    }

    val ratioX = actualWidth / options.outWidth.toFloat()
    val ratioY = actualHeight / options.outHeight.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f

    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

    val canvas = Canvas(scaledBitmap)
    canvas.setMatrix(scaleMatrix)
    canvas.drawBitmap(
        bmp, middleX - bmp.width / 2,
        middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG)
    )
    bmp.recycle()

    val matrix = Matrix()

    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
    }

    scaledBitmap = Bitmap.createBitmap(
        scaledBitmap, 0, 0, scaledBitmap.width,
        scaledBitmap.height, matrix, true
    )

    return scaledBitmap
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        inSampleSize *= 2
        val halfHeight = height / 2
        val halfWidth = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

private fun getOrientation(inputStream: InputStream): Int {
    try {
        val exif = ExifInterface(inputStream)

        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    } catch (e: Throwable) {

    }

    return ExifInterface.ORIENTATION_NORMAL
}