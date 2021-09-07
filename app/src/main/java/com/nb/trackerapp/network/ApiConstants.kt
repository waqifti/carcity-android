package com.nb.trackerapp.network

import android.location.Location

object ApiConstants {
    const val BASE_URL = "http://18.118.199.175:9000/"
    const val NO_INTERNET_MSG = "No internet connection"
    const val RESPONSE_TYPE_EMPTY = "response_type_empty"
    const val RESPONSE_TYPE_PARAMS = "response_type_params"
    const val LOGIN_TAG = "login_tag"
    const val GET_LOCATION = "get_location"
    const val UPDATE_LOCATION = "update_location"

    var IS_LOCATION_ENABLED = false
    var USER_LOCATION:Location? = null
}