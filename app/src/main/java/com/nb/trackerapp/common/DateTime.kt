package com.nb.trackerapp.common

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import com.nb.trackerapp.common.`interface`.OnItemSelectedListener
import com.nb.trackerapp.network.ApiConstants
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class DateTime {
    companion object{
        private const val ServerDateTimeFormat = "MMM dd, yyyy hh:mm:ss a"
        private const val DateFormat = "MMM dd, yyyy"
        private const val TimeFormat12 = "hh:mm:ss a"
        private const val TimeFormat24 = "HH:mm"

        fun getCurrentDateTime(): String {
            val simpleDateFormat = SimpleDateFormat(ServerDateTimeFormat, Locale.getDefault())
            return simpleDateFormat.format(Date())
            //return DateFormat.getDateTimeInstance().format(Date()).format(ServerDateTimeFormat)
        }

        fun showDatePickerDialog(context: Context,itemSelectedListener: OnItemSelectedListener){
            val calendar = Calendar.getInstance()
            val mYear = calendar.get(Calendar.YEAR)
            val mMonth = calendar.get(Calendar.MONTH)
            val mDay = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(context,{ view, year, month, dayOfMonth ->
                try {
                    val dateCalendar = Calendar.getInstance()
                    dateCalendar.set(year,month,dayOfMonth)
                    showTimePickerDialog(context,dateCalendar,itemSelectedListener)
                }catch (e:Exception){}
            },mYear,mMonth,mDay).show()
        }

        private fun showTimePickerDialog(context: Context,dateCalendar:Calendar,itemSelectedListener: OnItemSelectedListener){
            val calendar = Calendar.getInstance()
            val mHour = calendar.get(Calendar.HOUR_OF_DAY)
            val mMinute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(context,{ view, hourOfDay, minute ->
                try {
                    val time = "$hourOfDay:$minute"
                    itemSelectedListener.onItemSelected(ApiConstants.DATE_TIME_PICKER,formatDateTime(dateCalendar,time))
                }catch (e:Exception){}
            },mHour,mMinute,false).show()
        }

        private fun formatDateTime(dateCalendar:Calendar,timeCalendar: String) : String{
            return try {
                val dateFormat = SimpleDateFormat(DateFormat, Locale.getDefault())
                val parsedDate = dateFormat.format(dateCalendar.time)

                val timeFormat24 = SimpleDateFormat(TimeFormat24, Locale.getDefault())
                val timeFormat12 = SimpleDateFormat(TimeFormat12, Locale.getDefault())
                val parsedTime = timeFormat24.parse(timeCalendar)

                "$parsedDate ${timeFormat12.format(parsedTime)}"
            }catch (e:Exception){ "" }
        }
    }
}