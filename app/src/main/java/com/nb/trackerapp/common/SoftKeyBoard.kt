package com.nb.trackerapp.common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import java.lang.Exception

class SoftKeyBoard {
    companion object {
        private fun hide(view: View?, activity: Activity){
            try {
                val manager: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(view!!.windowToken, 0)
            }catch (e: Exception){ }
        }

        private fun hide(activity: Activity){
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }

        fun hideKeyboard(view: View?, activity: Activity){
            activity.runOnUiThread {
                hide(activity)
                hide(view,activity)
            }
        }
    }
}