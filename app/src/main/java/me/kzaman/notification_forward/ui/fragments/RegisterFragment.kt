package me.kzaman.notification_forward.ui.fragments

import android.os.Bundle
import me.kzaman.notification_forward.R
import me.kzaman.notification_forward.base.BaseFragment
import me.kzaman.notification_forward.databinding.FragmentRegisterBinding

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    override val layoutId: Int = R.layout.fragment_register

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
        mActivity = requireActivity()
    }
}