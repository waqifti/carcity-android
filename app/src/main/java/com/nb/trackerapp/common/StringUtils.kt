package com.nb.trackerapp.common

class StringUtils {
    companion object{
        fun isNotEmpty(string: String?) : Boolean{
            return string != null && string != "null" && string != ""
        }

        fun isEmpty(string: String?) : Boolean{
            return string == null && string == "null" && string == ""
        }
    }
}