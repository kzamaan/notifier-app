package me.kzaman.notification_forward.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import me.kzaman.notification_forward.R
import me.kzaman.notification_forward.base.BaseActivity
import me.kzaman.notification_forward.utils.visible


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var rlToolbar: RelativeLayout
    private lateinit var tvTitle: TextView
    private lateinit var ivBackButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeApp()
    }

    override fun initializeApp() {

        rlToolbar = findViewById(R.id.toolbar_root)
        tvTitle = findViewById(R.id.tv_toolbar_title)
        ivBackButton = findViewById(R.id.iv_back_button)
        // checking for last page
        // if last page home screen will be launched
        if (!isNotificationServiceEnabled()) {
            startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "notificationForwardId",
                "notificationForwardChannelName",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "CHANNEL_DESCRIPTION"
            }
            val nManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, "notificationForwardId")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Initial Notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line...")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1000, builder.build())
    }

    override fun setToolbarTitle(title: String) {
        setToolbarTitle(title, tvTitle)
    }

    override fun hideToolbar() {
        rlToolbar.visibility = View.GONE
    }

    override fun showToolbar(isBackButton: Boolean) {
        ivBackButton.visible(isBackButton)
        rlToolbar.visibility = View.VISIBLE
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":").toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}