package com.softxilla.notification_forwarder.ui

import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import com.softxilla.notification_forwarder.R
import com.softxilla.notification_forwarder.base.BaseActivity
import com.softxilla.notification_forwarder.database.MessageDatabaseHelper
import com.softxilla.notification_forwarder.database.SharedPreferenceManager
import com.softxilla.notification_forwarder.network.NetworkHelper
import com.softxilla.notification_forwarder.utils.LoadingUtils
import com.softxilla.notification_forwarder.utils.syncOfflineMessageToDatabase
import com.softxilla.notification_forwarder.utils.visible
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var prefManager: SharedPreferenceManager
    private lateinit var rlToolbar: Toolbar
    private lateinit var tvTitle: TextView
    private lateinit var ivBackButton: ImageView
    private lateinit var messageDatabaseHelper: MessageDatabaseHelper
    private lateinit var userInfoDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        messageDatabaseHelper = MessageDatabaseHelper(this)
        loadingUtils = LoadingUtils(this)
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
        // store user id in shared preference
        userInfoDialog = Dialog(this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
        userInfoDialog.setContentView(R.layout.dialog_user_info_layout)
        if (userInfoDialog.window != null) {
            userInfoDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        userInfoDialog.setCancelable(false)
        if (prefManager.getUserPhone().isEmpty()) {
            userInfoDialog.show()
            val userName = userInfoDialog.findViewById<EditText>(R.id.userNameInputField)
            val userPhone = userInfoDialog.findViewById<EditText>(R.id.userPhoneInputField)
            val saveUserInfo = userInfoDialog.findViewById<AppCompatButton>(R.id.saveUserInfo)
            saveUserInfo.setOnClickListener {
                prefManager.setNotifierUserInfo(userName.text.toString(), userPhone.text.toString())
                userInfoDialog.dismiss()
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.syncMessage -> {
                syncOfflineMessage()
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun syncOfflineMessage() {
        val helper = NetworkHelper(applicationContext)
        if (helper.isNetworkConnected()) {
            syncOfflineMessageToDatabase(applicationContext, prefManager.getUserPhone())
        } else {
            Toast.makeText(this, "No Internet Connection...", Toast.LENGTH_SHORT).show()
            Log.d("status", "Offline, No Internet")
        }
    }
}