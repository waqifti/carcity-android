package com.nb.trackerapp.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.widget.AppCompatTextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.nb.trackerapp.R
import com.nb.trackerapp.base.AppConstants
import com.nb.trackerapp.base.AppSession
import com.nb.trackerapp.common.`interface`.OnApiResponseListener
import com.nb.trackerapp.common.`interface`.OnDialogClickListener
import com.nb.trackerapp.data.LoginData
import com.nb.trackerapp.network.ApiConstants
import com.nb.trackerapp.views.dialogs.Dialog
import org.json.JSONObject

class AuthenticationActivity : AppCompatActivity(),OnDialogClickListener,OnApiResponseListener {
    private lateinit var loginDialog:LoginData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        val versionTv = findViewById<AppCompatTextView>(R.id.auth_versionTv)
        loginDialog = LoginData(this,window.decorView,this,this)

        // check if user is already login
        if(AppSession.isUserLogin(this)){ loginDialog.moveToMainActivity() }
        else{
            // binding data
            versionTv.text = "Version : ${Build.VERSION.RELEASE}"
            loginDialog.bindData()

            // request Permissions
            requestToWhiteListApp()
            requestPermission()
        }
    }

    override fun onDialogClick(dialogTag: String?, data: Any?) {
        when(dialogTag){
            AppConstants.PERMISSIONS_REQUIRED->{ requestPermission() }
        }
    }

    override fun onApiResponse(responseTag: String, jsonObject: JSONObject) {
        when(responseTag){
            ApiConstants.LOGIN_TAG->{
                loginDialog.moveToMainActivity(jsonObject.getString("response"))
            }
        }
    }

    private fun requestPermission(){
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (!p0!!.areAllPermissionsGranted()){
                        Dialog.showMessage(this@AuthenticationActivity,getString(R.string.permissions_required),
                            getString(R.string.alert),AppConstants.PERMISSIONS_REQUIRED,
                            this@AuthenticationActivity)
                    }else{ requestBackgroundLocationPermission() }
                }

                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?,
                                                                p1: PermissionToken?,) {
                    p1?.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun requestBackgroundLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        if (!p0!!.areAllPermissionsGranted()){
                            Dialog.showMessage(this@AuthenticationActivity,getString(R.string.permissions_required),
                                getString(R.string.alert),AppConstants.PERMISSIONS_REQUIRED,
                                this@AuthenticationActivity)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?,
                                                                    p1: PermissionToken?,) {
                        p1?.continuePermissionRequest()
                    }
                })
                .check()
        }
    }

    @SuppressLint("BatteryLife")
    private fun requestToWhiteListApp(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            if(!powerManager.isIgnoringBatteryOptimizations(packageName)){
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }
}