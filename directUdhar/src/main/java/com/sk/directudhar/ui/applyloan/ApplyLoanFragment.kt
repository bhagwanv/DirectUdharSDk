package com.sk.directudhar.ui.applyloan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sk.directudhar.databinding.FragmentApplyLoanBinding
import com.sk.directudhar.databinding.FragmentMyAccountBinding
import com.sk.directudhar.ui.MainActivitySDk

class ApplyLoanFragment:Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding:FragmentApplyLoanBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk= context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentApplyLoanBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {

    }
}