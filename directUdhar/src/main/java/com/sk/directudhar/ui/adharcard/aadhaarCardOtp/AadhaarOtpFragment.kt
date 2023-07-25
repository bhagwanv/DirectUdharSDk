package com.sk.directudhar.ui.adharcard.aadhaarCardOtp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentAadharOtpBinding
import com.sk.directudhar.ui.adharcard.UpdateAadhaarInfoRequestModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class AadhaarOtpFragment : Fragment() {

    private lateinit var activitySDk: MainActivitySDk
    private lateinit var mBinding: FragmentAadharOtpBinding
    private lateinit var aadhaarOtpViewModel: AadhaarOtpViewModel
    private var otp: String = ""
    private val args: AadhaarOtpFragmentArgs by navArgs()

    @Inject
    lateinit var aadhaarOtpFactory: AadhaarOtpFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAadharOtpBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectAadhaarOtp(this)
        aadhaarOtpViewModel =
            ViewModelProvider(this, aadhaarOtpFactory)[AadhaarOtpViewModel::class.java]

        aadhaarOtpViewModel.postResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(activitySDk)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    activitySDk.toast(it.errorMessage)
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    it.data.Msg?.let { it1 -> activitySDk.toast(it1) }
                    if (it.data.Result!!) {
                        activitySDk.toast(it.data.Msg!!)
                        activitySDk.checkSequenceNo(it.data.Data.SequenceNo)
                    } else {
                        activitySDk.toast(it.data.Msg!!)
                    }
                }
            }
        }

        mBinding.btnChangeAadhaarNumber.setOnClickListener {
            findNavController().popBackStack()
        }

        mBinding.btnNext.setOnClickListener {
            Log.i("TAG", "get Otp>>> ${mBinding.customOTPView.getOTP()}")
            otp = mBinding.customOTPView.getOTP()
            aadhaarOtpViewModel.validateOtp(otp)
        }

        aadhaarOtpViewModel.getAadhaarResult().observe(activitySDk) { result ->
            if (result.equals(Utils.AADHAAR_OTP_VALIDATE_SUCCESSFULLY)) {
                aadhaarOtpViewModel.aadharVerification(
                    AadharVerificationRequestModel(
                        otp, args.requestId, UpdateAadhaarInfoRequestModel(
                            leadMasterId = SharePrefs.getInstance(activitySDk)
                                ?.getInt(SharePrefs.LEAD_MASTERID)!!,
                            aadharNo = args.aadhaarNumber
                        )
                    )
                )
            } else {
                activitySDk.toast(result)
            }
        }
    }
}