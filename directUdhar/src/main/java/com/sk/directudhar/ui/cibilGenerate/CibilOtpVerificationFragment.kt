package com.sk.directudhar.ui.cibilGenerate

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentCibilOtpVerificationBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class CibilOtpVerificationFragment : Fragment() {
    @Inject
    lateinit var cibilGenerateFactory: CibilGenerateFactory

    @Inject
    lateinit var dialog: AppDialogClass

    lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentCibilOtpVerificationBinding? = null
    private val args: CibilOtpVerificationFragmentArgs by navArgs()
    private lateinit var cibilGenerateViewModel: CibilGenerateViewModel
    private var stgOneHitId = ""
    private var stgTwoHitId = ""
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (mBinding == null) {
            mBinding = FragmentCibilOtpVerificationBinding.inflate(inflater, container, false)

        }
        initView()
        startCountdown()
        return mBinding!!.root
    }

    private fun initView() {
        stgOneHitId = args.stgOneHitId
        stgTwoHitId = args.stgTwoHitId
        mBinding!!.tvMsg.text = "Enter the verification code we just sent to ${args.mobileNumber}"
        val component = DaggerApplicationComponent.builder().build()
        component.injectCibilOtpVerify(this)
        cibilGenerateViewModel = ViewModelProvider(
            this,
            cibilGenerateFactory
        )[CibilGenerateViewModel::class.java]
        setToolBar()
        setObserver()

        mBinding!!.btnVerifyOtp.setOnClickListener {
            val otp = mBinding!!.customOTPView.getOTP()
            if (otp.isNullOrEmpty()) {
                activitySDk.toast("Please enter otp")
            } else {
                cibilGenerateViewModel.callOtpVerify(
                    CibilOTPVerifyRequestModel(SharePrefs.getInstance(activitySDk)?.getInt(SharePrefs.LEAD_MASTERID)!!,stgOneHitId,stgTwoHitId,otp)
                )
            }
        }
        mBinding!!.tvResend.setOnClickListener {
            startCountdown()
            cibilGenerateViewModel.callGenOtp(SharePrefs.getInstance(activitySDk)?.getInt(SharePrefs.LEAD_MASTERID)!!)

        }
    }

    private fun setObserver() {
        cibilGenerateViewModel.getOptVerifyResponse.observe(viewLifecycleOwner) {
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
                            SharePrefs.getInstance(activitySDk)?.putInt(
                                SharePrefs.LEAD_MASTERID,
                                it.Data.LeadMasterId
                            )
                            activitySDk.toast(it.Msg)
                            activitySDk.checkSequenceNo(it.Data.SequenceNo)
                        } else {
                            mBinding!!.customOTPView.clearOTP()
                            activitySDk.toast(it.Msg)
                        }
                    }
                }
            }
        }

        cibilGenerateViewModel.getGenOptResponse.observe(viewLifecycleOwner) {
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
                            stgOneHitId = it.Data.stgOneHitId
                            stgTwoHitId = it.Data.stgTwoHitId
                        } else {
                            dialog.alertDialog(activitySDk,it.Msg,"Yes")
                            //activitySDk.toast(it.Msg)
                        }
                    }

                }
            }
        }

    }

    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "CIBIL OTP Verification"
       activitySDk.toolbar.navigationIcon = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }

    private fun startCountdown() {
        mBinding!!.tvResend.visibility = View.GONE
        mBinding!!.tvTimer.visibility = View.VISIBLE
       val countDownTimer = object : CountDownTimer(Utils.countdownDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                mBinding!!.tvTimer.text = "$secondsRemaining seconds"
            }
            override fun onFinish() {
                mBinding!!.tvResend.visibility = View.VISIBLE
                mBinding!!.tvTimer.visibility = View.GONE
            }
        }
        countDownTimer.start()
    }
}