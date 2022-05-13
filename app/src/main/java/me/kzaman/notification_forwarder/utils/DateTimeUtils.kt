package me.kzaman.notification_forwarder.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar


fun getDateFromString(strDate: String, dateFormat: String): Date {
    var date: Date? = null

    @SuppressLint("SimpleDateFormat")
    val format = SimpleDateFormat(dateFormat)
    try {
        date = format.parse(strDate)
    } catch (e: ParseException) {
        Log.d("dateException", e.toString())
        e.printStackTrace()
    }
    return date!!
}

@SuppressLint("SimpleDateFormat")
fun dateFormat(value: Date, outPutPattern: String): String {
    val dateFormat = SimpleDateFormat(outPutPattern)
    var result: String? = null
    try {
        result = dateFormat.format(value)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return result!!
}

fun currentDate(): Date {
    var date = Date()
    val calendar = Calendar.getInstance()
    calendar.time = date
    date = calendar.time
    return date
}

const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
fun compareDatesWithCurrentDate(maxDate: String, minDate: String): Boolean {
    return try {
        val startDate: Date = getDateFromString(minDate, YYYY_MM_DD_HH_MM_SS)
        val endDate: Date = getDateFromString(maxDate, YYYY_MM_DD_HH_MM_SS)
        val currentDate: Date =
            getDateFromString(dateFormat(currentDate(), YYYY_MM_DD_HH_MM_SS), YYYY_MM_DD_HH_MM_SS)
        currentDate.after(startDate) && currentDate.before(endDate)
    } catch (e: Exception) {
        false
    }
}