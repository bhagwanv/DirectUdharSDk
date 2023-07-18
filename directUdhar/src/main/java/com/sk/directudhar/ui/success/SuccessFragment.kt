package com.sk.directudhar.ui.success

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sk.directudhar.databinding.FragmentAadhaarCardBinding
import com.sk.directudhar.databinding.FragmentApplyLoanBinding
import com.sk.directudhar.databinding.FragmentCibilScoreBinding
import com.sk.directudhar.databinding.FragmentMyAccountBinding
import com.sk.directudhar.databinding.FragmentSuccessBinding
import com.sk.directudhar.ui.MainActivitySDk

class SuccessFragment:Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding:FragmentSuccessBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk= context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSuccessBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {

    }
}