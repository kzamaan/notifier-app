package com.softxilla.notification_forwarder.ui.fragments

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.softxilla.notification_forwarder.R
import com.softxilla.notification_forwarder.adapter.MessageAdapter
import com.softxilla.notification_forwarder.base.BaseFragment
import com.softxilla.notification_forwarder.data.model.LocalMessage
import com.softxilla.notification_forwarder.database.MessageDatabaseHelper
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
    private lateinit var userInfoDialog: Dialog
    private lateinit var databaseHelper: MessageDatabaseHelper
    private lateinit var messageAdapter: MessageAdapter

    override val layoutId: Int = R.layout.fragment_user_history
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
        mActivity = requireActivity()
        loadingUtils = LoadingUtils(mContext)
        databaseHelper = MessageDatabaseHelper(mContext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        binding.lifecycleOwner = viewLifecycleOwner
        initializeApp()
    }

    override fun initializeApp() {
        // store user id in shared preference
        userInfoDialog = Dialog(mContext, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
        userInfoDialog.setContentView(R.layout.dialog_user_info_layout)

        if (userInfoDialog.window != null) {
            userInfoDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        userInfoDialog.setCancelable(false)
        if (prefManager.getUserPhone().isEmpty()) {
            binding.userInfoCard.visibility = View.GONE
            userInfoDialog.show()
            val userName = userInfoDialog.findViewById<EditText>(R.id.userNameInputField)
            val userPhone = userInfoDialog.findViewById<EditText>(R.id.userPhoneInputField)
            val saveUserInfo = userInfoDialog.findViewById<AppCompatButton>(R.id.saveUserInfo)
            saveUserInfo.setOnClickListener {
                if (userName.text.isNotEmpty() && userPhone.text.isNotEmpty()) {
                    prefManager.setNotifierUserInfo(
                        userName.text.toString(),
                        userPhone.text.toString()
                    )
                    binding.tvUserName.text = userName.text
                    binding.tvPhoneNumber.text = userPhone.text
                    userInfoDialog.dismiss()
                    Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show()
                    binding.userInfoCard.visibility = View.VISIBLE
                } else {
                    Toast.makeText(mContext, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }

            }
        } else {
            val countMessage = databaseHelper.getUnSyncedMessage().count
            binding.tvUserName.text = if (countMessage > 0) {
                "${prefManager.getUserName()} ($countMessage)"
            } else {
                prefManager.getUserName()
            }
            binding.tvPhoneNumber.text = prefManager.getUserPhone()
            binding.ivUserImage.loadImage("https://ui-avatars.com/api/?name=${prefManager.getUserName()}")
        }

        binding.syncOfflineMessage.setOnClickListener {
            // rotate sync button
            binding.syncOfflineMessage.animate()
                .rotation(binding.syncOfflineMessage.rotation - 360f)
                .setDuration(1000).start()

            val helper = NetworkHelper(mContext)
            if (helper.isNetworkConnected()) {
                syncOfflineMessageToDatabase(mContext, prefManager.getUserPhone(), true)
            } else {
                Toast.makeText(mContext, "No Internet Connection...", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvUserName.setOnClickListener {
            refreshUserUnSyncMessage()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshUserUnSyncMessage()
            loadLocalMessage()
            // stop refreshing after 1 second
            Thread {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }.start()
        }
        messageAdapter = MessageAdapter(arrayListOf(), mContext)
        binding.rvMessageHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messageAdapter
        }
        loadLocalMessage()
    }

    private fun loadLocalMessage() {
        val messages = databaseHelper.getAllMessages()
        val localMessageList = ArrayList<LocalMessage>()
        if (messages.moveToFirst()) {
            do {
                val rowId = messages.getColumnIndex(MessageDatabaseHelper.ID)
                val androidTitle = messages.getColumnIndex(MessageDatabaseHelper.ANDROID_TITLE)
                val androidText = messages.getColumnIndex(MessageDatabaseHelper.ANDROID_TEXT)
                val createdAt = messages.getColumnIndex(MessageDatabaseHelper.CREATED_AT)
                val status = messages.getColumnIndex(MessageDatabaseHelper.STATUS)
                val item = LocalMessage(
                    messages.getString(rowId),
                    messages.getString(androidTitle),
                    messages.getString(androidText),
                    messages.getString(createdAt),
                    messages.getString(status).toInt()
                )
                localMessageList.add(item)
            } while (messages.moveToNext())
            messageAdapter.setMessages(localMessageList)
        }
    }

    private fun refreshUserUnSyncMessage(): Int {
        val countMessage = databaseHelper.getUnSyncedMessage().count
        binding.tvUserName.text = if (countMessage > 0) {
            "${prefManager.getUserName()} ($countMessage)"
        } else {
            prefManager.getUserName()
        }
        return countMessage
    }
}