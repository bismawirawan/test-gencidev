package test.gencidev.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun Long.toCurrency(currency: String = "", locale: Locale = Locale("id")): String {
    val formatter = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
    with(formatter) {
        decimalFormatSymbols = decimalFormatSymbols.apply {
            currencySymbol = currency
        }
        maximumFractionDigits = 0
    }
    return "Rp ${formatter.format(this)}"
}

fun currencyRupiah(price: String = "0"): String {
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    return "Rp ${formatRupiah.format(price.toLong())}"
}

fun toRupiah(currency: String = "0"): String {
    val check = currency.indexOf('.')
    return if (check < 0) currency.toLong().toCurrency() else currency.substring(0, check).toLong().toCurrency()
}

fun toRupiah(currency: Long = 0): String {
    return currency.toCurrency()
}

fun toRupiah(currency: Int = 0): String {
    return currency.toLong().toCurrency()
}

fun toDiscount(price: Int = 0): String {
    return "$price off"
}

fun toDiscountValue(data: String): Int {
    try {
        val promo = data.replace("%", "")
        return promo.toInt()
    } catch (e: Exception) {
        return 0
    }

}

fun toDiscount(price: String): String {
    return "$price off"
}

val Throwable.errorMessage: String
    get() = message ?: "Terjadi Kesalahan"

fun File.toRequestBody(fieldName: String, mediaType: String = "multipart/form-data"): MultipartBody.Part {
    val reqFile = RequestBody.create(mediaType.toMediaTypeOrNull(), this)
    return MultipartBody.Part.createFormData(fieldName, name, reqFile)
}

fun String.toRequestBody(): RequestBody {
    return RequestBody.create("text/plain".toMediaTypeOrNull(), this)
}

fun File.toRequestFile(fieldName: String, mediaType: String = "*/*"): MultipartBody.Part {
    val reqFile = RequestBody.create(mediaType.toMediaTypeOrNull(), this)
    return MultipartBody.Part.createFormData(fieldName, name, reqFile)
}

//val pendingState = MutableLiveData<UiState>()

//fun pendingAction(timer: Long = 1500) {
//    pendingState.value = UiState.Loading
//    GlobalScope.launch(Dispatchers.IO) {
//        delay(timer)
//        withContext(Dispatchers.Main) {
//            pendingState.value = UiState.Success
//        }
//    }
//}

fun copyClipboard(context: Context, data: String) {

    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", data)
    clipboardManager.setPrimaryClip(clipData)

    Toast.makeText(context, "Data tersalin di clipboard", Toast.LENGTH_SHORT).show()

}

fun getDateTimeNow(): String {
    val currentTime = Calendar.getInstance().time
    return currentTime.toString()
}

fun isLog(msg: String) {
    Log.e("cek log:", msg)
}