package com.linagora.android.linshare.view.widget

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.linagora.android.linshare.R

fun Toast.makeCustomToast(context: Context, message: String, duration: Int): Toast {
    val viewToast: View = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null)
    val tv: TextView = viewToast.findViewById(R.id.tv_text)
    tv.text = message

    view = viewToast
    this.duration = duration
    setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0)
    return this
}
