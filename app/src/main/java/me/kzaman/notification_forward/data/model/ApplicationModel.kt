package me.kzaman.notification_forward.data.model

import android.graphics.drawable.Drawable


data class ApplicationModel(
    val appName: String,
    val packageName: String,
    val appIcon: Drawable
)