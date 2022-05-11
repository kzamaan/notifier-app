package me.kzaman.notification_forward.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import me.kzaman.notification_forward.base.BaseActivity
import me.kzaman.notification_forward.database.SharedPreferenceManager
import me.kzaman.notification_forward.databinding.ActivitySettingsBinding
import me.kzaman.notification_forward.network.Resource
import me.kzaman.notification_forward.ui.viewModel.ProfileViewModel
import me.kzaman.notification_forward.utils.LoadingUtils
import me.kzaman.notification_forward.utils.handleNetworkError
import me.kzaman.notification_forward.utils.startNewActivityAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.kzaman.notification_forward.BuildConfig
import me.kzaman.notification_forward.R
import me.kzaman.notification_forward.utils.visible
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {
    @Inject
    lateinit var prefManager: SharedPreferenceManager

    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var binding: ActivitySettingsBinding

    private val tvTitleStr: String = "Settings"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initializeApp()

        loadingUtils = LoadingUtils(this)

        val versionName = BuildConfig.VERSION_NAME
        binding.appVersion.text = versionName

        val ivBackButton = binding.toolbarRoot.ivBackButton

        binding.toolbarRoot.tvToolbarTitle.text = tvTitleStr
        ivBackButton.visible(true)

        ivBackButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initializeApp() {
        getUserProfile()
        viewModel.profile.observe(this) {
            loadingUtils.isLoading(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.tvName.text = it.value.data.toString()
                }
                is Resource.Failure -> handleNetworkError(it, this) {
                    getUserProfile()
                }
                else -> Log.d("unknownError", "Unknown Error")
            }
        }

        binding.userLogout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logout()
                prefManager.clearAll()
                viewModel.clearAllTable()
                startNewActivityAnimation(AuthActivity::class.java)
            }
        }
    }

    override fun setToolbarTitle(title: String) {}

    private fun getUserProfile() {
        viewModel.getUserProfile()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
            overridePendingTransition(0, R.anim.animation_slide_out_right)
        }
    }
}