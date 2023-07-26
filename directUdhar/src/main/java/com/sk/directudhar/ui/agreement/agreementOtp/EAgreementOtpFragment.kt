package com.sk.directudhar.ui.agreement.agreementOtp

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentAgreementOtpBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.countdownDuration
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class EAgreementOtpFragment : Fragment() {

    private lateinit var activitySDk: MainActivitySDk
    private lateinit var mBinding: FragmentAgreementOtpBinding
    private lateinit var eAgreementOtpViewModel: EAgreementOtpViewModel
    @Inject
    lateinit var eAgreementOtpFactory: EAgreementOtpFactory

    private val args:EAgreementOtpFragmentArgs by navArgs()


    private lateinit var countDownTimer: CountDownTimer
     lateinit var reSendButton:Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAgreementOtpBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectAgreementOtp(this)
        eAgreementOtpViewModel =
            ViewModelProvider(this, eAgreementOtpFactory)[EAgreementOtpViewModel::class.java]

        mBinding.tvMobileNo.setText(args.mobileNo)
        reSendButton=mBinding.btnResendOtp

        startCountdown()

        mBinding.btnNext.setOnClickListener {
            Log.i("TAG", "get Otp>>> ${mBinding.customOTPView.getOTP()}")
            eAgreementOtpViewModel .validateOtp(mBinding.customOTPView.getOTP())
        }

        eAgreementOtpViewModel.getAgreementResult().observe(activitySDk) { result ->
            if (result.equals(Utils.EAGREEMENT_OTP_VALIDATE_SUCCESSFULLY)) {
                eAgreementOtpViewModel.eAgreementVerification(
                    EAgreementOtpResquestModel(true,SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID),mBinding.customOTPView.getOTP().toInt(),args.otpTxnNo)
                )
            } else {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            }
        }

        eAgreementOtpViewModel.eAgreementOtpVerificationResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(activitySDk)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    Toast.makeText(activitySDk, it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    if (it.data.Result!=null){
                        if (it.data.Msg!=null){
                            activitySDk.toast(it.data.Msg)
                        }
                    }


                }
            }
        }
        mBinding.btnResendOtp.setOnClickListener {
            mBinding.btnResendOtp.visibility=View.GONE
            eAgreementOtpViewModel.sendOtp(args.mobileNo)
            startCountdown()
        }
    }

    private fun startCountdown() {


        reSendButton.isEnabled = false
        countDownTimer = object : CountDownTimer(countdownDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                mBinding.tvTimer.text = "$secondsRemaining seconds"
            }

            override fun onFinish() {
                reSendButton.isEnabled = true
                reSendButton.text = "Resend OTP"
                mBinding.btnResendOtp.visibility=View.VISIBLE

            }
        }

        // Start the countdown
        countDownTimer.start()
    }

}