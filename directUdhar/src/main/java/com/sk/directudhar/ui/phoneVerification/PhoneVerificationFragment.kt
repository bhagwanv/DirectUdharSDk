package com.sk.directudhar.ui.phoneVerification

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentPhoneVerificationBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class PhoneVerificationFragment : Fragment() {
    @Inject
    lateinit var phoneVerificationFactory: PhoneVerificationFactory

    @Inject
    lateinit var dialog: AppDialogClass

    lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentPhoneVerificationBinding? = null
    private lateinit var phoneVerificationViewModel: PhoneVerificationViewModel
    private var mobileNumber = ""
    private var accountId: Long = 0
    private var flag: Int = 0  //outstanding=1 and Paid =2 ,  Next Due =3

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
            mBinding = FragmentPhoneVerificationBinding.inflate(inflater, container, false)
        }
        val component = DaggerApplicationComponent.builder().build()
        component.injectPhoneVerification(this)
        phoneVerificationViewModel = ViewModelProvider(
            this,
            phoneVerificationFactory
        )[PhoneVerificationViewModel::class.java]
        initView()
        return mBinding!!.root
    }

    private fun initView() {
        mBinding!!.tvTermsOfUse.setOnClickListener {

        }
         val text = SpannableString("By Proceeding, you agree Terms & Conditions.")
        //text.setSpan(UnderlineSpan(), 25, 44, 0)
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(activitySDk, R.color.blue_variant1)), 25, 44, 0)
        mBinding!!.tvTermsOfUse.text = text
        mobileNumber = SharePrefs.getInstance(activitySDk)?.getString(SharePrefs.MOBILE_NUMBER)!!
        mBinding!!.etMobileNumber.text = mobileNumber
        setToolBar()
        setObserver()

        mBinding!!.cbTermsOfUse.setOnClickListener {
            if (mBinding!!.cbTermsOfUse.isChecked) {
                mBinding!!.btnGenOtp.isClickable = true
                mBinding!!.btnGenOtp.isEnabled = true
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                mBinding!!.btnGenOtp.backgroundTintList = tintList
            } else {
                mBinding!!.btnGenOtp.isClickable = false
                mBinding!!.btnGenOtp.isEnabled = false
                val tintList =
                    ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                mBinding!!.btnGenOtp.backgroundTintList = tintList
            }
        }
        mBinding!!.btnGenOtp.setOnClickListener {
            var mobileNumber = mBinding!!.etMobileNumber.text.toString()
            if (mobileNumber.isNullOrEmpty()) {
                activitySDk.toast("Please enter mobile number")
            } else if (!mBinding!!.cbTermsOfUse.isChecked) {
                activitySDk.toast("Please Check Term and Condition")
            } else {
                phoneVerificationViewModel.callGenOtp(mobileNumber)
            }
        }
    }

    private fun setObserver() {
        phoneVerificationViewModel.getGenOptResponse.observe(viewLifecycleOwner) {
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
                            //  activitySDk.toast(it.Msg)
                            val action =
                                PhoneVerificationFragmentDirections.actionPhoneVerificationFragment(
                                    mobileNumber,
                                    it.Data.TxnNo
                                )
                            findNavController().navigate(action)
                        } else {
                            //activitySDk.toast(it.Msg)
                            dialog.alertDialog(activitySDk,it.Msg,"Yes")
                           /* dialog.setOnContinueCancelClick(object : AppDialogClass.OnContinueClicked {
                                override fun onContinueClicked() {

                                }
                            })*/
                        }
                    }

                }
            }
        }
    }

    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "Phone Verification"
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }
}