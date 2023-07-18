package com.sk.directudhar.ui.myaccount

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sk.directudhar.databinding.FragmentMyAccountBinding
import com.sk.directudhar.ui.MainActivitySDk

class MyAccountFragment:Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding:FragmentMyAccountBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk= context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMyAccountBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {

    }
}