package com.nb.trackerapp.data

import android.app.Activity
import android.content.Intent
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.nb.trackerapp.MainActivity
import com.nb.trackerapp.R
import com.nb.trackerapp.base.AppConstants
import com.nb.trackerapp.base.AppIntents
import com.nb.trackerapp.base.AppSession
import com.nb.trackerapp.common.StringUtils
import com.nb.trackerapp.common.`interface`.OnApiResponseListener
import com.nb.trackerapp.common.showErrorMessage
import com.nb.trackerapp.models.User
import com.nb.trackerapp.network.ApiConstants
import com.nb.trackerapp.network.ApiUrl
import com.nb.trackerapp.network.NetworkCall

class LoginData(private val activity: Activity,private val view: View,private val apiResponseListener: OnApiResponseListener) {
    private val optionLayout = view.findViewById<LinearLayout>(R.id.login_optionLayout)
    private val userTypeEt = view.findViewById<AppCompatEditText>(R.id.login_userTypeEt)
    private val phoneNumberEt = view.findViewById<AppCompatEditText>(R.id.login_phoneNumberEt)
    private val passwordEt = view.findViewById<AppCompatEditText>(R.id.login_passwordEt)

    fun bindData(){
        // setting views
        userTypeEt.isClickable = true
        userTypeEt.isFocusable = false
        userTypeEt.inputType = InputType.TYPE_NULL

        // bind buttons
        bindButtons()
    }

    private fun bindButtons(){
        // user type click
        userTypeEt.setOnClickListener {
            optionLayout.visibility = if(optionLayout.visibility == View.VISIBLE){ View.GONE }
            else{ View.VISIBLE }
        }

        // customer tv click
        view.findViewById<AppCompatTextView>(R.id.login_customerTv).setOnClickListener {
            userTypeEt.setText(activity.getString(R.string.customer))
        }

        // service provider tv click
        view.findViewById<AppCompatTextView>(R.id.login_providerTv).setOnClickListener {
            userTypeEt.setText(activity.getString(R.string.service_provider))
        }

        // login btn
        view.findViewById<AppCompatButton>(R.id.login_btn).setOnClickListener {
            if(validateData()){ setLoginCall() }
        }
    }

    private fun validateData() : Boolean{
        return when{
            phoneNumberEt.text.isNullOrEmpty()->{
                showErrorMessage(activity,"Enter phone number")
                false
            }
            passwordEt.text.isNullOrEmpty()->{
                showErrorMessage(activity,"Enter password")
                false
            }
            userTypeEt.text.isNullOrEmpty()->{
                showErrorMessage(activity,"Select user type")
                false
            }
            else->{ true }
        }
    }

    private fun setLoginCall(){
        val params = HashMap<String,Any>()
        params["cell"] = phoneNumberEt.text.toString()
        params["password"] = passwordEt.text.toString()
        params["ut"] = userTypeEt.text.toString().replace(" ","")

        NetworkCall.enqueueCall(activity,ApiUrl.getLoginUrl(),ApiConstants.RESPONSE_TYPE_PARAMS,ApiConstants.LOGIN_TAG,
        params,apiResponseListener)
    }

    fun moveToMainActivity(userToken:String){
        val user = User(userToken,userTypeEt.text.toString().replace(" ",""))
        AppConstants.CURRENT_USER = user
        AppSession.setLoginStatus(activity,true)
        AppSession.setCurrentUser(activity, user)
        AppIntents.moveToActivity(activity,true, Intent(activity,MainActivity::class.java))
    }

    fun moveToMainActivity(){
        AppIntents.moveToActivity(activity,true, Intent(activity,MainActivity::class.java))
    }
}