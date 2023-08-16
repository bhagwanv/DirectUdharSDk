package com.sk.directudhar.ui.mandate

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.databinding.FragmentEmandatSuccessBinding
import com.sk.directudhar.databinding.FragmentKycSuccessBinding
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpFactory
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpViewModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import javax.inject.Inject

class EMandateSuccessFragment : Fragment() {
    private lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentEmandatSuccessBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (mBinding == null) {
            mBinding = FragmentEmandatSuccessBinding.inflate(inflater, container, false)
            initView()
        }
        return mBinding!!.root
    }

    private fun initView() {
        setToolBar()
        mBinding!!.btnNext.setOnClickListener {
           // activitySDk.checkSequenceNo(2)
        }
    }

    private fun setToolBar() {
        activitySDk.toolbar.visibility = View.GONE
    }

}