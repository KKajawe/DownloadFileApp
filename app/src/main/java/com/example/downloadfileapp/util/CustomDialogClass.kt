package com.example.downloadfileapp.util

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.downloadfileapp.R

class CustomDialogClass(var c: Activity) {
    private val dialog: Dialog
    private val text: TextView
    private val dialogButton: Button
    @JvmField
    val dialogNegativeButton: Button
    var d: Dialog? = null
    var yes: Button? = null
    var no: Button? = null
    var txt_dia: TextView? = null
    fun showDialog(msg: String?) {
        text.text = msg
        dialogNegativeButton.visibility = View.GONE
        dialogButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun showDialogWithAction(msg: String?, okListener: View.OnClickListener?) {
        text.text = msg
        dialogNegativeButton.visibility = View.VISIBLE
        dialogNegativeButton.setOnClickListener { dialog.dismiss() }
        dialogButton.setOnClickListener(okListener)
        dialog.show()
    }

    fun showDialogWithSingleActionBtn(msg: String?, okListener: View.OnClickListener?) {
        text.text = msg
        dialogNegativeButton.visibility = View.GONE
        dialogButton.setOnClickListener(okListener)
        dialog.show()
    }

    fun cancelDialog() {
        dialog.dismiss()
    }

    init {
        // TODO Auto-generated constructor stub
        dialog = Dialog(c)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        text = dialog.findViewById<View>(R.id.txt_dia) as TextView
        dialogButton = dialog.findViewById<View>(R.id.btn_yes) as Button
        dialogNegativeButton = dialog.findViewById<View>(R.id.btn_no) as Button
    }
}