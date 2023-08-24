package com.sk.directudhar.ui.cibilGenerate

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentCibiGenerateBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class CibilGenerateFragment : Fragment() {
    @Inject
    lateinit var cibilGenerateFactory: CibilGenerateFactory

    @Inject
    lateinit var dialog: AppDialogClass

    lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentCibiGenerateBinding? = null
    private lateinit var cibilGenerateViewModel: CibilGenerateViewModel

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
            mBinding = FragmentCibiGenerateBinding.inflate(inflater, container, false)
            val component = DaggerApplicationComponent.builder().build()
            component.injectCibilGenerate(this)
            cibilGenerateViewModel = ViewModelProvider(
                this,
                cibilGenerateFactory
            )[CibilGenerateViewModel::class.java]
        }
        initView()
        termsAndConditions()
        return mBinding!!.root
    }
    fun termsAndConditions(){
        dialog.setOnContinueCancelClick(object : AppDialogClass.OnContinueClicked {
            override fun onContinueClicked(isAgree: Boolean) {
                mBinding!!.cbTermsOfUse.isChecked = isAgree
            }
        })
        val text = SpannableString("I have read and agree to Credit Score Terms of Use and hereby appoint Direct Udhaar as my authorised representative to receive my credit...")
        text.setSpan(ForegroundColorSpan(Color.BLUE), 69, 83, 0)
        mBinding!!.tvTermsOfUse.text = text
        mBinding!!.tvTermsOfUse.setOnClickListener {
            if (!activitySDk.privacyPolicyText.isNullOrEmpty()){
                dialog.termsAndAgreementPopUp(activitySDk, activitySDk.privacyPolicyText)
            }else{
                activitySDk.toast("No Privacy Policy Found")
            }
        }
    }
    private fun initView() {
        mBinding!!.etMobileNumber.text = SharePrefs.getInstance(activitySDk)?.getString(SharePrefs.MOBILE_NUMBER)!!
        setToolBar()
        setObserver()
        mBinding!!.btnGenerateCibil.setOnClickListener {
            if (mBinding!!.cbTermsOfUse.isChecked) {
                cibilGenerateViewModel.callGenOtp(
                    SharePrefs.getInstance(activitySDk)
                        ?.getInt(SharePrefs.LEAD_MASTERID)!!
                )
            }else{
                activitySDk.toast("Please Check Term and Condition")
            }
        }
        mBinding!!.cbTermsOfUse.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isChecked) {
                mBinding!!.btnGenerateCibil.isClickable = true
                mBinding!!.btnGenerateCibil.isEnabled = true
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                mBinding!!.btnGenerateCibil.backgroundTintList = tintList
            } else {
                mBinding!!.btnGenerateCibil.isClickable = false
                mBinding!!.btnGenerateCibil.isEnabled = false
                val tintList =
                    ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                mBinding!!.btnGenerateCibil.backgroundTintList = tintList
            }
        }
    }

    private fun setObserver() {
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
                            val action =
                                CibilGenerateFragmentDirections.actionCibilGenerateFragmentToCibilOtpVerificationFragment(
                                    "",
                                    it.Data.stgOneHitId,
                                    it.Data.stgTwoHitId,
                                )
                            findNavController().navigate(action)
                        } else {
                            activitySDk.toast(it.Msg)
                        }
                    }

                }
            }
        }
    }

    private fun setToolBar() {
        activitySDk.toolbar.visibility = View.GONE
        activitySDk.toolbarTitle.text = ""
        activitySDk.toolbar.navigationIcon = null
    }

    override fun onPause() {
        super.onPause()
        activitySDk.toolbar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }
}