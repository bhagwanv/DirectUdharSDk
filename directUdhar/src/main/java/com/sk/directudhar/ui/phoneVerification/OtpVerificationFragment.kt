package com.sk.directudhar.ui.phoneVerification

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
import com.sk.directudhar.databinding.FragmentOtpVerificationBinding
import com.sk.directudhar.databinding.FragmentPhoneVerificationBinding
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpFragmentArgs
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.countdownDuration
import com.sk.directudhar.utils.Utils.Companion.toast
import org.json.JSONObject
import javax.inject.Inject

class OtpVerificationFragment : Fragment() {
    @Inject
    lateinit var phoneVerificationFactory: PhoneVerificationFactory

    @Inject
    lateinit var dialog: AppDialogClass

    lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentOtpVerificationBinding? = null
    private val args: OtpVerificationFragmentArgs by navArgs()

    private lateinit var phoneVerificationViewModel: PhoneVerificationViewModel
    private var mobileNumber = ""
    private var accountId: Long = 0
    private var flag: Int = 0
    private lateinit var countDownTimer: CountDownTimer

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
            mBinding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        }
        initView()
        startCountdown()
        return mBinding!!.root
    }

    private fun initView() {
        mBinding!!.tvMsg.text = "Enter the verification code we just sent to ${args.mobileNumber}"
        val component = DaggerApplicationComponent.builder().build()
        component.injectOtpVerify(this)
        phoneVerificationViewModel = ViewModelProvider(
            this,
            phoneVerificationFactory
        )[PhoneVerificationViewModel::class.java]
        setToolBar()
        setObserver()

        mBinding!!.btnVerifyOtp.setOnClickListener {
            val otp = mBinding!!.customOTPView.getOTP()
            if (otp.isNullOrEmpty()) {
                activitySDk.toast("Please enter otp")
            } else {
                phoneVerificationViewModel.callOtpVerify(args.mobileNumber, otp, args.txnNo)
            }
        }
        mBinding!!.tvResend.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setObserver() {
        phoneVerificationViewModel.getOptVerifyResponse.observe(viewLifecycleOwner) {
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
                            activitySDk.toast(it.Msg)
                        }
                    }
                }
            }
        }
    }

    private fun setToolBar() {
        activitySDk.ivDateFilterToolbar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(countdownDuration, 1000) {
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