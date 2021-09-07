package com.nb.trackerapp.common

import java.text.DateFormat
import java.util.*

class DateTime {
    companion object{
        //private const val ServerDateTimeFormat = "MMM dd yyyy, HH:mm"

        fun getCurrentDateTime(): String {
            /*val sdf = SimpleDateFormat(ServerDateTimeFormat, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date())*/
            return DateFormat.getDateTimeInstance().format(Date())
        }
    }
}