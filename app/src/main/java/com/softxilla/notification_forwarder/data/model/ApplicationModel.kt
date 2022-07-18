package com.softxilla.notification_forwarder.data.model

import android.graphics.drawable.Drawable


data class ApplicationModel(
    val appName: String,
    val packageName: String,
    val appIcon: Drawable
)