import java.text.SimpleDateFormat
import java.util.*

fun Long.toDateString(format: String, locale: Locale): String {
    val dateFormat = SimpleDateFormat(format, locale)

    return dateFormat.format(this)
}