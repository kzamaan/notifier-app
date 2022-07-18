package com.softxilla.notification_forwarder.ui

import android.content.ComponentName
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import com.softxilla.notification_forwarder.R
import com.softxilla.notification_forwarder.base.BaseActivity
import com.softxilla.notification_forwarder.utils.visible


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var rlToolbar: Toolbar
    private lateinit var tvTitle: TextView
    private lateinit var ivBackButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rlToolbar = findViewById(R.id.toolbar_root)
        tvTitle = findViewById(R.id.tv_toolbar_title)
        ivBackButton = findViewById(R.id.iv_back_button)
        setSupportActionBar(rlToolbar)
        rlToolbar.overflowIcon?.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )
        ivBackButton.setOnClickListener {
            onBackPressed()
        }
        initializeApp()
    }

    override fun initializeApp() {
        // checking for last page
        // if last page home screen will be launched
        if (!isNotificationServiceEnabled()) {
            startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
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

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
            overridePendingTransition(0, R.anim.animation_slide_out_right)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
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