package com.linagora.android.linshare.util

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatImageView

fun View.showKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun View.dismissKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun AppCompatImageView.startAnimationDrawable() {
    visibility = View.VISIBLE
    (background as? AnimationDrawable)?.start()
}

fun AppCompatImageView.stopAnimationDrawable() {
    (background as? AnimationDrawable)?.stop()
    visibility = View.GONE
}
