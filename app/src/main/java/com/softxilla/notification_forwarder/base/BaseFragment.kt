package com.softxilla.notification_forwarder.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.softxilla.notification_forwarder.interfaces.InitialComponent
import com.softxilla.notification_forwarder.utils.LoadingUtils

abstract class BaseFragment<VB : ViewDataBinding> : Fragment(), InitialComponent {

    protected lateinit var mContext: Context
    protected lateinit var mActivity: Activity
    protected lateinit var baseActivity: BaseActivity
    protected lateinit var loadingUtils: LoadingUtils

    lateinit var mRootView: View
    lateinit var viewDataBinding: VB

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = viewDataBinding.root
        return mRootView
    }

    override fun initializeApp() {}
    override fun setToolbarTitle(title: String) {}
}