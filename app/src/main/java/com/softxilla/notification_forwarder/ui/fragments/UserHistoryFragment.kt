package com.softxilla.notification_forwarder.ui.fragments

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.softxilla.notification_forwarder.R
import com.softxilla.notification_forwarder.adapter.MessageAdapter
import com.softxilla.notification_forwarder.base.BaseFragment
import com.softxilla.notification_forwarder.data.model.LocalMessage
import com.softxilla.notification_forwarder.database.MessageDatabaseHelper
import com.softxilla.notification_forwarder.database.SharedPreferenceManager
import com.softxilla.notification_forwarder.databinding.FragmentUserHistoryBinding
import com.softxilla.notification_forwarder.network.Resource
import com.softxilla.notification_forwarder.ui.viewModel.CommonViewModel
import com.softxilla.notification_forwarder.utils.LoadingUtils
import com.softxilla.notification_forwarder.utils.handleNetworkError
import com.softxilla.notification_forwarder.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class UserHistoryFragment : BaseFragment<FragmentUserHistoryBinding>() {
    @Inject
    lateinit var prefManager: SharedPreferenceManager
    private lateinit var binding: FragmentUserHistoryBinding
    private lateinit var userInfoDialog: Dialog
    private lateinit var databaseHelper: MessageDatabaseHelper
    private lateinit var messageAdapter: MessageAdapter
    private val viewModel by viewModels<CommonViewModel>()

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
            binding.tvUserName.text = prefManager.getUserName()
            binding.tvPhoneNumber.text = prefManager.getUserPhone()
            binding.ivUserImage.loadImage("https://ui-avatars.com/api/?name=${prefManager.getUserName()}")
        }

        binding.syncOfflineMessage.setOnClickListener {
            // rotate sync button
            binding.syncOfflineMessage.animate()
                .rotation(binding.syncOfflineMessage.rotation - 360f)
                .setDuration(1000).start()
            syncOfflineMessageToDatabase()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
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
        viewModel.syncOfflineResponse.observe(viewLifecycleOwner) {
            loadingUtils.isLoading(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    val response = it.value
                    Toast.makeText(mContext, response.message, Toast.LENGTH_SHORT).show()

                    if (response.status) {
                        response.offlineIds.forEach { id ->
                            Log.d("offlineId", id)
                            databaseHelper.updateMessageStatus(id.toInt())
                            loadLocalMessage()
                        }
                    } else {
                        if (response.offlineIds.isNotEmpty()) {
                            response.offlineIds.forEach { id ->
                                databaseHelper.updateMessageStatus(id.toInt())
                                loadLocalMessage()
                            }
                        }
                    }
                }
                is Resource.Failure -> handleNetworkError(it, mActivity) {
                    syncOfflineMessageToDatabase()
                }
                else -> Log.d("unknownError", "Unknown Error")
            }
        }
    }

    private fun loadLocalMessage() {
        val messages = databaseHelper.getUnSyncedMessage()
        val localMessageList = ArrayList<LocalMessage>()
        if (messages.moveToFirst()) {
            binding.tvHistory.text = "Pending Message (${messages.count})"
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
        } else {
            messageAdapter.setMessages(arrayListOf())
            binding.tvHistory.text = "No Pending Message"
        }
    }

    private fun syncOfflineMessageToDatabase(): Boolean {
        val messages = databaseHelper.getUnSyncedMessage()
        val jsonArray = JSONArray()
        if (messages.moveToFirst()) {
            do {
                val messageObject = JSONObject()
                val rowId = messages.getColumnIndex(MessageDatabaseHelper.ID)
                val androidTitle = messages.getColumnIndex(MessageDatabaseHelper.ANDROID_TITLE)
                val androidText = messages.getColumnIndex(MessageDatabaseHelper.ANDROID_TEXT)
                val createdAt = messages.getColumnIndex(MessageDatabaseHelper.CREATED_AT)
                messageObject.put("offline_id", messages.getString(rowId))
                messageObject.put("android_title", messages.getString(androidTitle))
                messageObject.put("android_text", messages.getString(androidText))
                messageObject.put("created_at", messages.getString(createdAt))
                jsonArray.put(messageObject)
            } while (messages.moveToNext())

            val postObject = jsonArray.toString()
            Log.d("offlineMessage", postObject)

            viewModel.syncOfflineNotification(prefManager.getUserPhone(), postObject)
        } else {
            Toast.makeText(mContext, "No Pending Message", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}