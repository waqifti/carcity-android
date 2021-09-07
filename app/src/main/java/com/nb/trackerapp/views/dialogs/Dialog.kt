package com.nb.trackerapp.views.dialogs

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.nb.trackerapp.R
import com.nb.trackerapp.common.`interface`.OnDialogClickListener

class Dialog {
    companion object{
        var progressDialog:AlertDialog? = null

        fun showProgress(activity: Activity,message: String = "Loading...") {
            activity.runOnUiThread {
                val inflater =
                    activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val builder = AlertDialog.Builder(activity)
                val view = inflater.inflate(R.layout.dialog_progress, null)

                builder.setView(view)
                builder.create()
                val alertDialog = builder.show()
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                progressDialog = alertDialog
            }
        }

        fun showMessage(activity: Activity,message:String,title:String = "Alert",dialogTag:String? = null,
                       dialogResponseListener: OnDialogClickListener? = null){
            activity.runOnUiThread {
                val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val builder = AlertDialog.Builder(activity)
                val view = inflater.inflate(R.layout.dialog_message, null)

                val titleTv = view.findViewById<AppCompatTextView>(R.id.dialog_titleTv)
                val messageTv = view.findViewById<AppCompatTextView>(R.id.dialog_messageTv)
                titleTv.text = title
                messageTv.text = message

                builder.setView(view)
                builder.create()
                val alertDialog = builder.show()
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)

                // got it btn
                view.findViewById<AppCompatButton>(R.id.dialog_gotItBtn).setOnClickListener {
                    alertDialog.dismiss()
                    dialogTag?.let { dialogResponseListener?.onDialogClick(it) }
                }
            }
        }
    }
}