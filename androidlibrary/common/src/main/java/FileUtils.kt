import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileUtils {
    companion object
}

fun FileUtils.Companion.createUriProvider(
    context: Context,
    storageDir: String = Environment.DIRECTORY_PICTURES,
    provider: String,
    folder: String? = null,
    fileName: String
): Uri? {
    val path = FileUtils.createFile(context, storageDir, folder, fileName)

    if (path != null) {
        return FileProvider.getUriForFile(context, provider, path)
    }

    return null
}

fun FileUtils.Companion.copyStreamToFile(inputStream: InputStream, outputFile: File) {
    inputStream.use { input ->
        val outputStream = FileOutputStream(outputFile)
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024) // buffer size
            while (true) {
                val byteCount = input.read(buffer)
                if (byteCount < 0) break
                output.write(buffer, 0, byteCount)
            }
            output.flush()
        }
    }
}

fun FileUtils.Companion.deleteDir(
    context: Context,
    storageDir: String = Environment.DIRECTORY_PICTURES,
    folder: String? = null
) {
    val dir = context.getExternalFilesDir(storageDir)

    val path = if (dir != null) {
        if (folder != null) {
            dir.absolutePath + File.separator + folder
        } else {
            dir.absolutePath
        }
    } else {
        null
    }

    if (path != null) {
        deleteDir(File(path))
    }
}

fun FileUtils.Companion.deleteDir(dir: File): Boolean {
    if (dir.isDirectory) {
        val children = dir.list() ?: return dir.delete()

        for (i in children.indices) {
            val success: Boolean = FileUtils.deleteDir(File(dir, children[i]))
            if (!success) {
                return false
            }
        }
    }

    return dir.delete()
}

fun FileUtils.Companion.createFile(
    context: Context,
    storageDir: String = Environment.DIRECTORY_PICTURES,
    folder: String? = null,
    fileName: String
): File? {

    val dir = context.getExternalFilesDir(storageDir)

    val path = if (dir != null) {
        if (folder != null) {
            dir.absolutePath + File.separator + folder
        } else {
            dir.absolutePath
        }
    } else {
        null
    }

    if (path != null) {
        val filePath = File(path)

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val pathWithDirName = File(path, fileName)

        if (!pathWithDirName.exists()) {
            pathWithDirName.createNewFile()
        }

        return pathWithDirName
    }

    return null
}

fun FileUtils.Companion.mimeType(context: Context, uri: Uri): String? {
    return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        context.contentResolver.getType(uri)
    } else {
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())

        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())
    }
}





