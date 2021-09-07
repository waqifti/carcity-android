package com.nb.trackerapp.network

class ApiUrl {
    companion object{
        fun getLoginUrl():String{
            return ApiConstants.BASE_URL+"Login"
        }

        fun getUpdateLocationUrl(userType:String):String{
            return ApiConstants.BASE_URL+"Authenticated/$userType/UpdateLocation"
        }
    }
}