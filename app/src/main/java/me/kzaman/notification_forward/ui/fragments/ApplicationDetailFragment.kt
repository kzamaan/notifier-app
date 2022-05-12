package me.kzaman.notification_forward.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import me.kzaman.notification_forward.R
import me.kzaman.notification_forward.base.BaseFragment
import me.kzaman.notification_forward.databinding.FragmentApplicationDetailBinding
import me.kzaman.notification_forward.ui.MainActivity

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
        (activity as MainActivity).setToolbarTitle("Application Detail")

        val packageName = arguments?.get("packageName")
        binding.textView.text = "Package: $packageName"
    }

}