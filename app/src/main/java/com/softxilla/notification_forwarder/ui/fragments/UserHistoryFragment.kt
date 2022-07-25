package com.softxilla.notification_forwarder.ui.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.softxilla.notification_forwarder.R
import com.softxilla.notification_forwarder.adapter.MessageAdapter
import com.softxilla.notification_forwarder.base.BaseFragment
import com.softxilla.notification_forwarder.data.model.LocalMessage
import com.softxilla.notification_forwarder.data.response.OfflineResponse
import com.softxilla.notification_forwarder.database.MessageDatabaseHelper
import com.softxilla.notification_forwarder.database.SharedPreferenceManager
import com.softxilla.notification_forwarder.databinding.FragmentUserHistoryBinding
import com.softxilla.notification_forwarder.network.NetworkHelper
import com.softxilla.notification_forwarder.utils.LoadingUtils
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
                syncOfflineMessageToDatabase(mContext, prefManager.getUserPhone())
            } else {
                Toast.makeText(mContext, "No Internet Connection...", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvUserName.setOnClickListener {
            refreshUserUnSyncMessage()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadLocalMessage()
            refreshUserUnSyncMessage()
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
        val messages = databaseHelper.getUnSyncedMessage()
        val localMessageList = ArrayList<LocalMessage>()
        if (messages.moveToFirst()) {
            binding.tvHistory.text = "Pending Message"
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

    private fun refreshUserUnSyncMessage(): Int {
        val countMessage = databaseHelper.getUnSyncedMessage().count
        binding.tvUserName.text = if (countMessage > 0) {
            "${prefManager.getUserName()} ($countMessage)"
        } else {
            prefManager.getUserName()
        }
        return countMessage
    }

    private fun syncOfflineMessageToDatabase(
        mContext: Context,
        msgFrom: String,
    ): Boolean {
        val loadingUtils = LoadingUtils(mContext)
        val messages = databaseHelper.getUnSyncedMessage()
        val jsonArray = JSONArray()
        if (messages.moveToFirst()) {
            loadingUtils.isLoading(true)
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

            val postObject: MutableMap<String, String> = HashMap()
            postObject["messages"] = jsonArray.toString()
            postObject["msg_from"] = msgFrom
            Log.d("offlineMessage", postObject.toString())
            val url = "https://softxilla.com/api/sync-offline-message"
            val requestQueue = Volley.newRequestQueue(mContext)

            val jsonObjRequest: StringRequest =
                object : StringRequest(Method.POST, url,
                    Response.Listener {
                        loadingUtils.isLoading(false)
                        val offline = Gson().fromJson(it.toString(), OfflineResponse::class.java)
                        updateDatabaseStatus(mContext, offline)
                    }, Response.ErrorListener { error ->
                        loadingUtils.isLoading(false)
                        VolleyLog.d("volley", "Error: " + error.message)
                        error.printStackTrace()
                    }) {
                    override fun getBodyContentType(): String {
                        return "application/x-www-form-urlencoded; charset=UTF-8"
                    }

                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        return postObject
                    }
                }
            requestQueue.add(jsonObjRequest)
        } else {
            Toast.makeText(mContext, "No Pending Message", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun updateDatabaseStatus(mContext: Context, offlineResponse: OfflineResponse) {
        Log.d("offlineResponse", offlineResponse.toString())
        Toast.makeText(mContext, offlineResponse.message, Toast.LENGTH_SHORT).show()
        if (offlineResponse.status) {
            offlineResponse.offlineIds.forEach {
                Log.d("offlineId", it)
                databaseHelper.updateMessageStatus(it.toInt())
                refreshUserUnSyncMessage()
                loadLocalMessage()
            }
        } else {
            if (offlineResponse.offlineIds.isNotEmpty()) {
                offlineResponse.offlineIds.forEach {
                    databaseHelper.updateMessageStatus(it.toInt())
                    refreshUserUnSyncMessage()
                    loadLocalMessage()
                }
            }
        }
    }
}