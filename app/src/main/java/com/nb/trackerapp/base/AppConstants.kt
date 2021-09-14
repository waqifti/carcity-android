package com.nb.trackerapp.base

import com.nb.trackerapp.models.User

object AppConstants {
    const val PERMISSIONS_REQUIRED = "permission_required"
    const val USER_DATA = "current_user.txt"
    const val AUTH_SHARED_PREFERENCE = "auth_sp"
    const val LOGIN_STATUS = "login_status"
    const val DEVICE_TOKEN = "device_token"

    var CURRENT_USER: User? = null
    var APP_STATE = "BACKGROUND"
}