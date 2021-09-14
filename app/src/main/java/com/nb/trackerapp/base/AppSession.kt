package com.nb.trackerapp.base

import android.content.Context
import com.nb.trackerapp.models.User
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class AppSession {
    companion object{
        fun setLoginStatus(context: Context, status:Boolean){
            val sp = context.getSharedPreferences(AppConstants.AUTH_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putBoolean(AppConstants.LOGIN_STATUS,status)
            editor.apply()
        }

        fun isUserLogin(context: Context) : Boolean{
            val sp = context.getSharedPreferences(AppConstants.AUTH_SHARED_PREFERENCE, Context.MODE_PRIVATE)
            return sp.getBoolean(AppConstants.LOGIN_STATUS,false)
        }

        fun setDeviceToken(context: Context,token: String){
            val sp = context.getSharedPreferences(AppConstants.AUTH_SHARED_PREFERENCE,Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString(AppConstants.DEVICE_TOKEN,token)
            editor.apply()
        }

        fun getDeviceToken(context: Context) : String?{
            val sp = context.getSharedPreferences(AppConstants.AUTH_SHARED_PREFERENCE,Context.MODE_PRIVATE)
            return sp.getString(AppConstants.DEVICE_TOKEN,null)
        }

        fun setCurrentUser(context: Context,user: User){
            try {
                val fileOutputStream = context.openFileOutput(AppConstants.USER_DATA, Context.MODE_PRIVATE)
                val objectOutputStream = ObjectOutputStream(fileOutputStream)
                objectOutputStream.writeObject(user)
                objectOutputStream.close()
                fileOutputStream.close()
            }catch(e:java.lang.Exception){}
        }

        private fun getUser(context: Context): User?{
            var user: User? = null
            if (context.getFileStreamPath(AppConstants.USER_DATA).exists()){
                try {
                    val fileInputStream = context.openFileInput(AppConstants.USER_DATA)
                    val objectInputStream = ObjectInputStream(fileInputStream)
                    user = objectInputStream.readObject() as User
                    objectInputStream.close()
                    fileInputStream.close()
                }catch(e:Exception){}
            }

            return user
        }

        fun getCurrentUser(context: Context):User?{
            AppConstants.CURRENT_USER = AppConstants.CURRENT_USER ?: run { getUser(context) }
            return AppConstants.CURRENT_USER
        }
    }
}