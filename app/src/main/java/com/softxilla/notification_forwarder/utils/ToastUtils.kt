package com.softxilla.notification_forwarder.utils

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.softxilla.notification_forwarder.R

private fun Activity.setToast(layout: View) {
    val toast = Toast(this)
    toast.setGravity(Gravity.BOTTOM, 0, 20)
    toast.duration = Toast.LENGTH_LONG
    toast.view = layout
    toast.show()
}

fun Activity.getDrawableBackgroundColor(mView: View, color: Int) {
    val gradientDrawable = mView.background as GradientDrawable
    gradientDrawable.setColor(ContextCompat.getColor(this, color))
    gradientDrawable.setStroke(0, ContextCompat.getColor(this, color))
    mView.invalidate()
}

/**
 * ...display success toast message
 */
fun toastSuccess(activity: Activity, message: String) {
    val inflater = activity.layoutInflater
    val layout: View =
        inflater.inflate(R.layout.layout_toast_message, activity.findViewById(R.id.toast_layout_root))

    val tvBody = layout.findViewById<TextView>(R.id.tv_message)
    val tvTitle = layout.findViewById<TextView>(R.id.tv_title)
    val ivIcon = layout.findViewById<ImageView>(R.id.iv_icon)

    tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.success_color))
    ivIcon.background = ContextCompat.getDrawable(activity, R.drawable.bg_toast_icon)
    activity.getDrawableBackgroundColor(ivIcon, R.color.success_bg_color)
    ivIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_success_toast_icon))

    tvTitle.text = activity.resources.getString(R.string.success)
    tvBody.text = message

    activity.setToast(layout)
}

/**
 * ...display error toast message
 */

fun toastError(activity: Activity, message: String) {
    val inflater = activity.layoutInflater
    val layout: View =
        inflater.inflate(R.layout.layout_toast_message, activity.findViewById(R.id.toast_layout_root))

    val tvBody = layout.findViewById<TextView>(R.id.tv_message)
    val tvTitle = layout.findViewById<TextView>(R.id.tv_title)
    val ivIcon = layout.findViewById<ImageView>(R.id.iv_icon)

    tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.error_color))
    ivIcon.background = ContextCompat.getDrawable(activity, R.drawable.bg_toast_icon)
    activity.getDrawableBackgroundColor(ivIcon, R.color.error_bg_color)
    ivIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_error_toast))

    tvTitle.text = activity.resources.getString(R.string.error)
    tvBody.text = message

    activity.setToast(layout)
}

/**
 * ...display information toast message
 */
fun toastInfo(activity: Activity, message: String) {
    val inflater = activity.layoutInflater
    val layout: View =
        inflater.inflate(R.layout.layout_toast_message, activity.findViewById(R.id.toast_layout_root))

    val tvBody = layout.findViewById<TextView>(R.id.tv_message)
    val tvTitle = layout.findViewById<TextView>(R.id.tv_title)
    val ivIcon = layout.findViewById<ImageView>(R.id.iv_icon)

    tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.info_color))
    ivIcon.background = ContextCompat.getDrawable(activity, R.drawable.bg_toast_icon)
    activity.getDrawableBackgroundColor(ivIcon, R.color.info_bg_color)
    ivIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_info_toast))

    tvTitle.text = activity.resources.getString(R.string.info)
    tvBody.text = message

    activity.setToast(layout)
}

/**
 * ...display warning toast message
 */
fun toastWarning(activity: Activity, message: String) {
    val inflater = activity.layoutInflater
    val layout: View =
        inflater.inflate(R.layout.layout_toast_message, activity.findViewById(R.id.toast_layout_root))

    val tvBody = layout.findViewById<TextView>(R.id.tv_message)
    val tvTitle = layout.findViewById<TextView>(R.id.tv_title)
    val ivIcon = layout.findViewById<ImageView>(R.id.iv_icon)

    tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.warning_color))
    ivIcon.background = ContextCompat.getDrawable(activity, R.drawable.bg_toast_icon)
    activity.getDrawableBackgroundColor(ivIcon, R.color.warning_bg_color)
    ivIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_warning_toast))

    tvTitle.text = activity.resources.getString(R.string.warning)
    tvBody.text = message

    activity.setToast(layout)
}