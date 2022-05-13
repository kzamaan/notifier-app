package me.kzaman.notification_forwarder.ui.fragments

import android.os.Bundle
import me.kzaman.notification_forwarder.R
import me.kzaman.notification_forwarder.base.BaseFragment
import me.kzaman.notification_forwarder.databinding.FragmentRegisterBinding

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    override val layoutId: Int = R.layout.fragment_register

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
        mActivity = requireActivity()
    }
}