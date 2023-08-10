package com.sk.directudhar.ui.agreement.agreementOtp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.sk.directudhar.R
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

        tabLayouts()

       /* mBinding.tvMobileNo.setText(args.mobileNo)
        reSendButton=mBinding.btnResendOtp
*/
        startCountdown()

        mBinding.btnNext.setOnClickListener {
           /* Log.i("TAG", "get Otp>>> ${mBinding.customOTPView.getOTP()}")
            eAgreementOtpViewModel .validateOtp(mBinding.customOTPView.getOTP())*/
        }

        eAgreementOtpViewModel.getAgreementResult().observe(activitySDk) { result ->
            if (result.equals(Utils.EAGREEMENT_OTP_VALIDATE_SUCCESSFULLY)) {
               /* eAgreementOtpViewModel.eAgreementVerification(
                  //  EAgreementOtpResquestModel(true,SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID),mBinding.customOTPView.getOTP().toInt(),args.otpTxnNo)
                )*/
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
       /* mBinding.btnResendOtp.setOnClickListener {
            mBinding.btnResendOtp.visibility=View.GONE
            eAgreementOtpViewModel.sendOtp(args.mobileNo)
            startCountdown()
        }*/



        val signatureView: SignatureView = mBinding.signatureView
        mBinding.clearButton.setOnClickListener {
            signatureView.clearSignature()
        }


    }

    private fun startCountdown() {


        /* reSendButton.isEnabled = false
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
*/
        // Start the countdown
        //  countDownTimer.start()
    }


    fun tabLayouts(){
        mBinding.tabs.removeAllTabs()
        mBinding.tabs.addTab(mBinding.tabs.newTab().setText("Adhar E-Sign"))
        mBinding.tabs.addTab(mBinding.tabs.newTab().setText("OTP Verification"))

        mBinding.llAadharESign.visibility=View.VISIBLE
        mBinding.llOTPVerification.visibility=View.GONE
        mBinding.tabs.setTabTextColors( ContextCompat.getColor(activitySDk, R.color.blue_variant1),ContextCompat.getColor(activitySDk, R.color.white_variant) )
        mBinding .tabs.setOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {

                if (tab.position == 0) {
                    // selectedTab = 0
                    mBinding.llAadharESign.visibility=View.VISIBLE
                    mBinding.llOTPVerification.visibility=View.GONE
                   // mBinding.tabs.startAnimation(AnimationUtils.loadAnimation(activitySDk, R.anim.move_animation))
                } else {
                    // selectedTab = 1
                    mBinding.llOTPVerification.visibility=View.VISIBLE
                    mBinding.llAadharESign.visibility=View.GONE

                   // mBinding.tabs.startAnimation(AnimationUtils.loadAnimation(activitySDk, R.anim.move_animation))


                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }

}