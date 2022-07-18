package com.softxilla.notification_forwarder.ui.fragments

import android.os.Bundle
import com.softxilla.notification_forwarder.R
import com.softxilla.notification_forwarder.base.BaseFragment
import com.softxilla.notification_forwarder.databinding.FragmentRegisterBinding

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    override val layoutId: Int = R.layout.fragment_register

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
        mActivity = requireActivity()
    }
}