package com.nb.trackerapp.common

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateTime {
    companion object{
        private const val ServerDateTimeFormat = "MMM dd, yyyy HH:mm:ss a"

        fun getCurrentDateTime(): String {
            /*val simpleDateFormat = SimpleDateFormat(ServerDateTimeFormat, Locale.getDefault())
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return simpleDateFormat.format(Date())*/

            return DateFormat.getDateTimeInstance().format(Date()).format(ServerDateTimeFormat)
        }
    }
}