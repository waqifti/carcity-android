package com.nb.trackerapp.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.nb.trackerapp.R
import com.nb.trackerapp.common.SoftKeyBoard

class AppIntents {
    companion object{
        fun moveToActivity(activity: Activity, isFinish:Boolean, intent: Intent){
            activity.runOnUiThread {
                if (isFinish) {activity.finish()}
                activity.startActivity(intent)
            }
        }

        fun addFragment(frameId:Int,fragment: Fragment,fragmentManager: FragmentManager){
            fragmentManager.beginTransaction().add(frameId,fragment).commit()
        }

        fun moveToForwardFragment(view: View?, context: Context, frameId:Int, fragment: Fragment,
                                  fragmentManager: FragmentManager){
            (context as Activity).runOnUiThread {
                SoftKeyBoard.hideKeyboard(view,context as Activity)
                if(!context.isFinishing){
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                        R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(frameId, fragment, null).addToBackStack(null).commit()
                }
            }
        }
    }
}