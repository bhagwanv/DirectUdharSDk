package com.sk.directudhar.ui.cibilscore

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
import com.sk.directudhar.ui.MainActivitySDk

class CibilScoreFragment:Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding:FragmentCibilScoreBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk= context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCibilScoreBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {

    }
}