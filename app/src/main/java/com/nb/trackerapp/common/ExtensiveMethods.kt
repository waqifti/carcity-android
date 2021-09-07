package com.nb.trackerapp.common

import android.app.Activity
import com.nb.trackerapp.views.dialogs.Dialog

fun showErrorMessage(activity: Activity,message:String){
    Dialog.showMessage(activity,message)
}