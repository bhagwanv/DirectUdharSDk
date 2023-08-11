package com.sk.directudhar.ui.adharcard.aadhaarCardOtp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.R
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
    private var mBinding: FragmentAadharOtpBinding? = null
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
        if (mBinding == null) {
            mBinding = FragmentAadharOtpBinding.inflate(inflater, container, false)
            initView()
        }
        return mBinding!!.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectAadhaarOtp(this)
        aadhaarOtpViewModel =
            ViewModelProvider(this, aadhaarOtpFactory)[AadhaarOtpViewModel::class.java]

        setToolBar()
        setObserver()

        mBinding!!.tvResendAadhaarOtp.setOnClickListener {
            findNavController().popBackStack()
        }

        mBinding!!.btnVerifyAadhaarOtp.setOnClickListener {
            Log.i("TAG", "get Otp>>> ${mBinding!!.customOTPView.getOTP()}")
            otp = mBinding!!.customOTPView.getOTP()
            aadhaarOtpViewModel.validateOtp(otp)

        }
    }

    private fun setObserver() {
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
                    it.data.let {
                        if (it.Result) {
                            activitySDk.toast(it.Msg)
                            // activitySDk.checkSequenceNo(it.Data.SequenceNo)
                            val action = AadhaarOtpFragmentDirections.actionAadhaarOtpFragmentToKycSuccessFragment()
                            findNavController().navigate(action)
                        } else {
                           // activitySDk.toast(it.Msg)
                            val action = AadhaarOtpFragmentDirections.actionAadhaarOtpFragmentToKycFailedFragment()
                            findNavController().navigate(action)
                        }
                    }
                }
            }
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

    private fun setToolBar() {
        activitySDk.ivDateFilterToolbar.visibility = View.GONE
    }

    /*private val aadhaarOtpTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
           if (otp.length == 6) {
               val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
               mBinding!!.btnVerifyAadhaarOtp.backgroundTintList = tintList
           } else {
               val tintList = ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
               mBinding!!.btnVerifyAadhaarOtp.backgroundTintList = tintList
           }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (otp.length == 6) {
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                mBinding!!.btnVerifyAadhaarOtp.backgroundTintList = tintList
            } else {
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                mBinding!!.btnVerifyAadhaarOtp.backgroundTintList = tintList
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (otp.length == 6) {
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                mBinding!!.btnVerifyAadhaarOtp.backgroundTintList = tintList
            } else {
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                mBinding!!.btnVerifyAadhaarOtp.backgroundTintList = tintList
            }
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }
}