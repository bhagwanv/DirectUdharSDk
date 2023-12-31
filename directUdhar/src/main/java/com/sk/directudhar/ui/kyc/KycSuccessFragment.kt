package com.sk.directudhar.ui.kyc

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.databinding.FragmentKycSuccessBinding
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpFactory
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpViewModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import javax.inject.Inject

class KycSuccessFragment : Fragment() {
    @Inject
    lateinit var aadhaarOtpFactory: AadhaarOtpFactory
    private lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentKycSuccessBinding? = null
    private lateinit var aadhaarOtpViewModel: AadhaarOtpViewModel
    private val args: KycSuccessFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (mBinding == null) {
            mBinding = FragmentKycSuccessBinding.inflate(inflater, container, false)
            initView()
            println("NavType>>${args.navType}")
        }
        return mBinding!!.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectKycSuccess(this)
        aadhaarOtpViewModel =
            ViewModelProvider(this, aadhaarOtpFactory)[AadhaarOtpViewModel::class.java]
        setToolBar()
        mBinding!!.btnNext.setOnClickListener {
             val action = KycSuccessFragmentDirections.actionKycSuccessFragmentToApplyLoanFragment(args.navType)
                findNavController().navigate(action)
        }
    }

    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "KYC Completed"
       activitySDk.toolbar.navigationIcon = null
    }

}