//DateUtils.kt

package com.creditos.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    // Convierte Date a String con formato dd/MM/yyyy
    fun formatDate(date: Date?): String? {
        return date?.let { dateFormat.format(it) }
    }

    // Convierte Date a String con formato dd/MM/yyyy HH:mm:ss
    fun formatDateTime(date: Date?): String? {
        return date?.let { dateTimeFormat.format(it) }
    }

    // Convierte String a Date con formato dd/MM/yyyy
    fun parseDate(dateString: String?): Date? {
        return try {
            dateString?.let { dateFormat.parse(it) }
        } catch (e: Exception) {
            null
        }
    }

    // Convierte String a Date con formato dd/MM/yyyy HH:mm:ss
    fun parseDateTime(dateTimeString: String?): Date? {
        return try {
            dateTimeString?.let { dateTimeFormat.parse(it) }
        } catch (e: Exception) {
            null
        }
    }
}
