package com.scg.hrst.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.scg.hrst.R


class HrstCustomToast(private val mCtx: Context) : Toast(mCtx) {


    override fun setView(view: View) {
        super.setView(view)
    }

    /*set custom view*/
    fun setCustomView(message: String) {
        val inflater = mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.hrst_custom_toast, null)
        view.findViewById<TextView>(R.id.tvToastMessage).text = message
//        view.tvToastMessage.text = message
        setView(view)
    }
}