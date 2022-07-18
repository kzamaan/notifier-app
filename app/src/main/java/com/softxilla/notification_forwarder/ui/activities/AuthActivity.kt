package com.softxilla.notification_forwarder.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.softxilla.notification_forwarder.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}