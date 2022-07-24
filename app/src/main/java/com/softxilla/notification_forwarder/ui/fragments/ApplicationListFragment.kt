package com.softxilla.notification_forwarder.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import com.softxilla.notification_forwarder.R
import com.softxilla.notification_forwarder.adapter.ApplicationAdapter
import com.softxilla.notification_forwarder.base.BaseActivity
import com.softxilla.notification_forwarder.base.BaseFragment
import com.softxilla.notification_forwarder.data.model.ApplicationModel
import com.softxilla.notification_forwarder.databinding.FragmentApplicationListBinding
import com.softxilla.notification_forwarder.ui.MainActivity
import com.softxilla.notification_forwarder.ui.activities.DashboardActivity
import com.softxilla.notification_forwarder.utils.hideSoftKeyboard
import java.text.Collator

@AndroidEntryPoint
class ApplicationListFragment : BaseFragment<FragmentApplicationListBinding>() {

    private lateinit var binding: FragmentApplicationListBinding
    private lateinit var applicationAdapter: ApplicationAdapter
    private val sCollator: Collator = Collator.getInstance()

    override val layoutId: Int = R.layout.fragment_application_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
        mActivity = requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        binding.lifecycleOwner = viewLifecycleOwner
        initializeApp()
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun initializeApp() {
        (activity as BaseActivity).showToolbar(false) //display toolbar
        (activity as BaseActivity).setToolbarTitle("Application List")

        //get a list of installed apps.
        val intent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        val packageManager = mActivity.packageManager
        val packages = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA)
        val applicationModel = ArrayList<ApplicationModel>()
        packages.forEach { packageInfo ->
            val item = ApplicationModel(
                appName = packageInfo.loadLabel(packageManager).toString(),
                packageName = packageInfo.activityInfo.packageName,
                appIcon = packageInfo.loadIcon(packageManager)
            )
            applicationModel.add(item)
        }

        applicationModel.sortWith { o1, o2 ->
            sCollator.compare(o1.appName, o2.appName)
        }

        applicationAdapter = ApplicationAdapter(applicationModel, mContext)
        binding.appList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = applicationAdapter
        }

        binding.etSearch.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                binding.ivCancelSearch.visibility = View.GONE
            } else {
                binding.ivCancelSearch.visibility = View.VISIBLE
            }
            applicationAdapter.filter.filter(it)
        }

        binding.ivCancelSearch.setOnClickListener {
            binding.etSearch.text = null
            hideSoftKeyboard(mContext, binding.etSearch)
            it.visibility = View.GONE
        }
    }
}