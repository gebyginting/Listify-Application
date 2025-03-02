package com.geby.listifyapplication.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {
    fun getCurrentDate() : String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun formatTime(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()) // Format awal
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Hanya jam & menit

            val date = inputFormat.parse(dateString) // Parsing ke Date
            outputFormat.format(date!!) // Format ulang
        } catch (e: Exception) {
            dateString // Jika error, return string asli
        }
    }
}