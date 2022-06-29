package me.kzaman.notification_forwarder.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import me.kzaman.notification_forwarder.R
import me.kzaman.notification_forwarder.base.BaseFragment
import me.kzaman.notification_forwarder.databinding.FragmentApplicationDetailBinding
import me.kzaman.notification_forwarder.ui.MainActivity

@AndroidEntryPoint
class ApplicationDetailFragment : BaseFragment<FragmentApplicationDetailBinding>() {

    private lateinit var binding: FragmentApplicationDetailBinding

    override val layoutId: Int = R.layout.fragment_application_detail

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

    @SuppressLint("SetTextI18n")
    override fun initializeApp() {
        (activity as MainActivity).showToolbar(true) //display toolbar

        val packageName = arguments?.get("packageName")
        val packageManager = (activity as MainActivity).packageManager

        val applicationInfo = packageManager.getApplicationInfo(packageName.toString(), 0)
        (activity as MainActivity).setToolbarTitle(applicationInfo.loadLabel(packageManager).toString())

        binding.activeBtn.setOnClickListener {
            Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show()
        }
    }

}