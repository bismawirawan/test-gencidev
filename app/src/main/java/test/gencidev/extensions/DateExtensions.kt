package test.gencidev.extensions

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private val dateFormat = ThreadLocal<SimpleDateFormat?>()
val indonesiaTimeZone: TimeZone = TimeZone.getTimeZone("Asia/Jakarta")

fun String.formatDate(from: String, to: String): String {
    try {
        val sdf = dateFormat.get() ?: SimpleDateFormat(from, Locale("id")).apply {
            dateFormat.set(this)
        }
        with(sdf) {
            applyPattern(from)
            val date = try {
                parse(this@formatDate)
            } catch (error: ParseException) {
                return this@formatDate
            }
            applyPattern(to)
            return format(date)
        }
    } catch (error: Exception) {
        return "-"
    }

}

@SuppressLint("SimpleDateFormat")
fun String.formatDateApi(format: String): String{
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        inputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val outputFormat = SimpleDateFormat(format)
        val date = inputFormat.parse(this)
        //    println(formattedDate) // prints 10-04-2018

        return outputFormat.format(date!!)
    } catch (e: Exception) {
        return "-"
    }
}

fun getAbbreviatedFromDateTime(dateTime: String, dateFormat: String, field: String): String? {
    val input = SimpleDateFormat(dateFormat)
    val output = SimpleDateFormat(field)
    try {
        val getAbbreviate = input.parse(dateTime)    // parse input
        return output.format(getAbbreviate)    // format output
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return null
}

fun String.formatDate(pattern: String = "EEEE, d MMMM yyyy"): String {
    try {
        val indonesia = Locale("id", "ID", "ID")
        val inputFormatDate = SimpleDateFormat("yyyy-MM-d", indonesia)
        val outputDateFormat = SimpleDateFormat(pattern, indonesia)
        val date = inputFormatDate.parse(this)
        outputDateFormat.timeZone = indonesiaTimeZone
        return outputDateFormat.format(date)
    } catch (error: Exception) {
        return "-"
    }

}

fun Date.formatDate(): String {
    try {
        val indonesia = Locale("id", "ID", "ID")
        val inputFormatDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", indonesia)

        val sdf = dateFormat.get() ?: SimpleDateFormat("dd MMM yyyy - hh:mm", indonesia).apply {
            dateFormat.set(this)
        }
        with(sdf) {
            return format(this@formatDate)
        }
    } catch (error: Exception) {
        return "-"
    }
}


fun String.formatDateLelang(): String {
    try {
        val indonesia = Locale("id", "ID", "ID")
        val inputFormatDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", indonesia)
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", indonesia)
        val date = inputFormatDate.parse(this)
        outputDateFormat.timeZone = indonesiaTimeZone
        return outputDateFormat.format(date)
    } catch (error: Exception) {
        return "-"
    }

}

fun String.formatTimeLelang(): String {
    try {
        val indonesia = Locale("id", "ID", "ID")
        val inputFormatDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", indonesia)
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", indonesia)
        val date = inputFormatDate.parse(this)
        outputDateFormat.timeZone = indonesiaTimeZone
        return outputDateFormat.format(date)
    } catch (error: Exception) {
        return "-"
    }

}

fun String.formatTimeRiwayatLelang(): String {

    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val waktu: Date = format.parse(this)

    // set Event Date
    val currentDate = Calendar.getInstance()
    val eventDate = Calendar.getInstance()

    eventDate.time = waktu

    val diff =  currentDate.timeInMillis - eventDate.timeInMillis

    // Change the milliseconds to days, hours, minutes and seconds
    val days = diff / (24 * 60 * 60 * 1000)
    val hours = diff / (1000 * 60 * 60) % 24
    val minutes = diff / (1000 * 60) % 60
    val seconds = (diff / 1000) % 60

    val fullHours = days * 24
    val jam = fullHours + hours

    return "${jam}j ${minutes}m ${seconds}d"

}

fun String.eventLelangBerakhir() : String {

    val calendar = Calendar.getInstance()
    val currentDate = Calendar.getInstance()

    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val eventBerakhir: Date = format.parse(this)
    calendar.time = eventBerakhir

    val diff = calendar.timeInMillis - currentDate.timeInMillis

    val days = diff / (24 * 60 * 60 * 1000)
    val hours = diff / (1000 * 60 * 60) % 24
    val minutes = diff / (1000 * 60) % 60
    val seconds = (diff / 1000) % 60

    val fullHours = days * 24
    val jam = fullHours + hours

//    return "${jam}j ${minutes}m ${seconds}d"
    return "Sisa waktu lelang $jam jam lagi"

}

fun String.convertTimeToTimeAgo(): String {

    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.timeZone = TimeZone.getTimeZone("UTC")
    val waktu: Date = format.parse(this)

    // set Event Date
    val currentDate = Calendar.getInstance()
    val eventDate = Calendar.getInstance()

    eventDate.time = waktu

    val diff =  currentDate.timeInMillis - eventDate.timeInMillis

    // Change the milliseconds to days, hours, minutes and seconds
    val days = diff / (24 * 60 * 60 * 1000)
    val hours = diff / (1000 * 60 * 60) % 24
    val minutes = diff / (1000 * 60) % 60
    val seconds = (diff / 1000) % 60

    if (days > 0) {
        return "$days hari yang lalu"
    } else if (hours > 0) {
        return "$hours jam yang lalu"
    } else if (minutes > 0) {
        return "$minutes menit yang lalu"
    } else {
        return "$seconds detik yang lalu"
    }
}

fun String.toDateData(pattern: String = "yyyy-MM-dd'T'hh:mm:ss"): Date? {
    val sdf = dateFormat.get() ?: SimpleDateFormat(pattern, Locale("id")).apply {
        dateFormat.set(this)
    }
    sdf.applyPattern(pattern)
    return try {
        sdf.parse(this)
    } catch (error: ParseException) {
        return null
    }
}

fun String.toDate(pattern: String = "yyyy-MM-dd'T'hh:mm:ss"): Date? {

    val sdf = dateFormat.get() ?: SimpleDateFormat(pattern, Locale("id")).apply {
        dateFormat.set(this)
    }
    sdf.applyPattern(pattern)
    return try {
        sdf.parse(this)
    } catch (error: ParseException) {
        return null
    }
}

fun Date.formatText(pattern: String = "yyyy-MM-dd'T'hh:mm:ss"): String {
    try {
        val sdf = dateFormat.get() ?: SimpleDateFormat(pattern, Locale("id")).apply {
            dateFormat.set(this)
        }
        sdf.applyPattern(pattern)
        with(sdf) {
            return format(this@formatText)
        }
    } catch (error: Exception) {
        return "-"
    }

}
