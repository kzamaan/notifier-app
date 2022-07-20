package com.softxilla.notification_forwarder.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.softxilla.notification_forwarder.R
import com.softxilla.notification_forwarder.base.BaseFragment
import com.softxilla.notification_forwarder.database.SharedPreferenceManager
import com.softxilla.notification_forwarder.databinding.FragmentUserHistoryBinding
import com.softxilla.notification_forwarder.network.NetworkHelper
import com.softxilla.notification_forwarder.utils.LoadingUtils
import com.softxilla.notification_forwarder.utils.loadImage
import com.softxilla.notification_forwarder.utils.syncOfflineMessageToDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserHistoryFragment : BaseFragment<FragmentUserHistoryBinding>() {
    @Inject
    lateinit var prefManager: SharedPreferenceManager
    private lateinit var binding: FragmentUserHistoryBinding

    override val layoutId: Int = R.layout.fragment_user_history
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
        mActivity = requireActivity()
        loadingUtils = LoadingUtils(mContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        binding.lifecycleOwner = viewLifecycleOwner
        initializeApp()
    }

    override fun initializeApp() {
        binding.tvUserName.text = prefManager.getUserName()
        binding.tvPhoneNumber.text = prefManager.getUserPhone()
        binding.ivUserImage.loadImage("https://ui-avatars.com/api/?name=${prefManager.getUserName()}")
        binding.syncOfflineMessage.setOnClickListener {
            // rotate sync button
            binding.syncOfflineMessage.animate().rotation(binding.syncOfflineMessage.rotation - 360f).setDuration(1000).start()

            val helper = NetworkHelper(mContext)
            if (helper.isNetworkConnected()) {
                syncOfflineMessageToDatabase(mContext, prefManager.getUserPhone())
            } else {
                Toast.makeText(mContext, "No Internet Connection...", Toast.LENGTH_SHORT).show()
                Log.d("status", "Offline, No Internet")
            }
        }
    }
}