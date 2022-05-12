package me.kzaman.notification_forward.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import me.kzaman.notification_forward.R
import me.kzaman.notification_forward.adapter.ApplicationAdapter
import me.kzaman.notification_forward.data.model.ApplicationModel
import me.kzaman.notification_forward.utils.hideSoftKeyboard


@AndroidEntryPoint
class ApplicationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var applicationAdapter: ApplicationAdapter
    private lateinit var etSearch: AppCompatEditText
    private lateinit var ivCancelSearch: AppCompatImageView

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.appList)
        etSearch = findViewById(R.id.et_search)
        ivCancelSearch = findViewById(R.id.iv_cancel_search)

        // checking for last page
        // if last page home screen will be launched
        if (!isNotificationServiceEnabled()) {
            startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        //get a list of installed apps.
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val applicationModel = ArrayList<ApplicationModel>()
        packages.forEach { packageInfo ->
            val item = ApplicationModel(
                appName = packageInfo.loadLabel(packageManager).toString(),
                packageName = packageInfo.packageName,
                appIcon = packageInfo.loadIcon(packageManager)
            )
            applicationModel.add(item)
        }

        applicationAdapter = ApplicationAdapter(applicationModel, this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = applicationAdapter
        }

        etSearch.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                ivCancelSearch.visibility = View.GONE
            } else {
                ivCancelSearch.visibility = View.VISIBLE
            }
            applicationAdapter.filter.filter(it)
        }

        ivCancelSearch.setOnClickListener {
            etSearch.text = null
            hideSoftKeyboard(this, etSearch)
            it.visibility = View.GONE
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