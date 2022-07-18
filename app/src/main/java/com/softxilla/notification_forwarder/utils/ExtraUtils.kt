package com.softxilla.notification_forwarder.utils

import org.json.JSONArray
import java.text.DecimalFormat


/**
 * ...add comma for amount
 * @param number decimal amount
 * @return amount with comma
 */
fun numberFormat(number: Double): String {
    val formatter = DecimalFormat(amountFormat(doubleValueFormat(number)))
    return formatter.format(number)
}

/**
 * ...get amount format
 * @param amount actual amount
 * @return amount format
 */
fun amountFormat(amount: String): String {
    return when (amount.length) {
        5 -> "##,###.##"
        6 -> "#,##,###.##"
        7 -> "##,##,###.##"
        8 -> "#,##,##,###.##"
        9 -> "##,##,##,###.##"
        10 -> "#,##,##,##,###.##"
        11 -> "##,##,##,##,###.##"
        4 -> "#,###.##"
        else -> "#,###.##"
    }
}

/**
 * get double format value with two decimal places
 * when decimal number is 0 then return int format
 * @param d double value
 * @return double format value
 */
fun doubleValueFormat(d: Double): String {
    return if (d.equals(d.toLong())) {
        String.format("%d", d.toLong())
    } else {
        String.format("%.2f", d)
    }
}

fun countJsonObject(json: String): Int {
    return try {
        val jsonArray = JSONArray(json)
        jsonArray.length()
    } catch (ex: Exception) {
        0
    }
}