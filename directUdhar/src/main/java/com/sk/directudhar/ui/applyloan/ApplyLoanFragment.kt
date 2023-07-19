package com.sk.directudhar.ui.applyloan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.databinding.FragmentApplyLoanBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.ui.myaccount.MyAccountViewModel
import com.sk.directudhar.utils.DaggerApplicationComponent
import javax.inject.Inject

class ApplyLoanFragment:Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding:FragmentApplyLoanBinding

    lateinit var applyLoanViewModel: ApplyLoanViewModel
    @Inject
    lateinit var applyLoanFactory: ApplyLoanFactory

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
        val component = DaggerApplicationComponent.builder().build()
        component.injectApplyLoan(this)
        applyLoanViewModel = ViewModelProvider(this, applyLoanFactory)[ApplyLoanViewModel::class.java]

    }
}