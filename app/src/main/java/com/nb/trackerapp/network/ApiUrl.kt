package com.nb.trackerapp.network

class ApiUrl {
    companion object{
        fun getLoginUrl():String{
            return ApiConstants.BASE_URL+"Login"
        }

        fun getUpdateLocationUrl(userType:String):String{
            return ApiConstants.BASE_URL+"Authenticated/$userType/UpdateLocation"
        }

        fun getJobTypesUrl():String{
            return ApiConstants.BASE_URL+"GetJobTypes"
        }

        fun getJobRequestUrl(userType:String):String{
            return ApiConstants.BASE_URL+"Authenticated/$userType/createJobRequest"
        }

        fun getJobDetailsUrl(userType:String):String{
            return ApiConstants.BASE_URL+"Authenticated/$userType/getJobDetails"
        }

        fun getAssignedJobDetailsUrl(userType:String):String{
            return ApiConstants.BASE_URL+"Authenticated/$userType/getAssignedJobDetails"
        }
    }
}