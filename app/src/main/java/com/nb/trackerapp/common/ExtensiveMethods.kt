package com.nb.trackerapp.common

import android.content.Context
import com.nb.trackerapp.views.dialogs.Dialog

fun showErrorMessage(context: Context,message:String){
    Dialog.showMessage(context,message)
}