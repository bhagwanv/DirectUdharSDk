package com.sk.directudhar.ui.mandate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sk.directudhar.databinding.FragmentAadhaarCardBinding
import com.sk.directudhar.databinding.FragmentApplyLoanBinding
import com.sk.directudhar.databinding.FragmentCibilScoreBinding
import com.sk.directudhar.databinding.FragmentEMandateBinding
import com.sk.directudhar.databinding.FragmentMyAccountBinding
import com.sk.directudhar.ui.MainActivitySDk

class EMandateFragment:Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding:FragmentEMandateBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk= context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEMandateBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {

    }
}