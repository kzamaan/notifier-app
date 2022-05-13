package me.kzaman.notification_forwarder.ui.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.kzaman.notification_forwarder.R
import me.kzaman.notification_forwarder.adapter.ApplicationAdapter
import me.kzaman.notification_forwarder.base.BaseFragment
import me.kzaman.notification_forwarder.data.model.ApplicationModel
import me.kzaman.notification_forwarder.databinding.FragmentApplicationListBinding
import me.kzaman.notification_forwarder.ui.MainActivity
import me.kzaman.notification_forwarder.utils.hideSoftKeyboard
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
        (activity as MainActivity).showToolbar(false) //display toolbar
        (activity as MainActivity).setToolbarTitle("Application List")

        val packageManager = (mActivity as MainActivity).packageManager

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