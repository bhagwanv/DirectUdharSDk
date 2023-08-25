package com.sk.directudhar.ui.adharcard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentAadhaarCardBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class AadhaarCardFragment : Fragment() {

    private lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentAadhaarCardBinding? = null
    private lateinit var aadhaarCardViewModel: AadhaarCardViewModel
    var aadharNo: String = ""
    @Inject
    lateinit var dialog: AppDialogClass
    @Inject
    lateinit var aadhaarCardFactory: AadhaarCardFactory

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
            mBinding = FragmentAadhaarCardBinding.inflate(inflater, container, false)
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
        val text = SpannableString("By Proceeding, you agree Terms & Conditions.")
        text.setSpan(ForegroundColorSpan(Color.BLUE), 25, 44, 0)
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
        val component = DaggerApplicationComponent.builder().build()
        component.injectAadhaarCard(this)
        aadhaarCardViewModel =
            ViewModelProvider(this, aadhaarCardFactory)[AadhaarCardViewModel::class.java]

        setToolBar()
        setObserver()

        mBinding!!.cbTermsOfUse.setOnCheckedChangeListener { buttonView, isChecked ->
            aadharNo = mBinding!!.etAadhaarNumber.text.toString().trim()
            aadharNo = aadharNo.replace(" ","")
            if (!aadharNo.isNullOrEmpty()) {
                if (buttonView.isChecked) {
                    mBinding!!.btnVerifyAadhaar.isClickable = true
                    mBinding!!.btnVerifyAadhaar.isEnabled = true
                    val tintList =
                        ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                    mBinding!!.btnVerifyAadhaar.backgroundTintList = tintList
                } else {
                    mBinding!!.btnVerifyAadhaar.isClickable = false
                    mBinding!!.btnVerifyAadhaar.isEnabled = false
                    val tintList =
                        ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                    mBinding!!.btnVerifyAadhaar.backgroundTintList = tintList
                }
            } else {
                mBinding!!.cbTermsOfUse.isChecked = false
                activitySDk.toast("Please enter aadhaar number")
            }
        }

        mBinding!!.btnVerifyAadhaar.setOnClickListener {
            aadharNo = mBinding!!.etAadhaarNumber.text.toString().trim()
            aadharNo = aadharNo.replace(" ","")
            aadhaarCardViewModel.validateAadhaar(
                aadharNo,
                mBinding!!.cbTermsOfUse.isChecked
            )
        }

        mBinding!!.tvManuallyUploadAadhaar.setOnClickListener {
            val action =
                AadhaarCardFragmentDirections.actionAadhaarFragmentToAadharManuallyUploadFragment()
            findNavController().navigate(action)
        }
    }

    private fun setObserver() {
        aadhaarCardViewModel.getAadhaarResult().observe(activitySDk) { result ->
            if (result.equals(Utils.AADHAAR_VALIDATE_SUCCESSFULLY)) {
                aadhaarCardViewModel.updateAadhaarInfo(
                    UpdateAadhaarInfoRequestModel(
                        aadharNo,
                         SharePrefs.getInstance(activitySDk)
                            ?.getInt(SharePrefs.LEAD_MASTERID)!!
                    )
                )
            } else {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            }
        }

        aadhaarCardViewModel.postResponse.observe(viewLifecycleOwner) {
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
                    it.data.let {
                        if (it.Result!!){
                            activitySDk.toast(it.Msg!!)
                            val action =  AadhaarCardFragmentDirections.actionAadhaarFragmentToAadharOtpFragment(
                                aadharNo,
                                it.DynamicData?.request_id!!
                            )
                            findNavController().navigate(action)
                        }else{
                            activitySDk.toast(it.Msg!!)
                        }
                    }
                }
            }
        }
    }

    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "Aadhaar Verification"
       activitySDk.toolbar.navigationIcon = null

    }

    private val aadhaarTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not used
        }

        override fun afterTextChanged(s: Editable?) {
            val aadhaarNumber = s.toString().trim()
            if (aadhaarNumber.length < 12) {
                aadharNo = ""
                /* val tintList =
                     ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                 mBinding!!.btnVerifyAadhaar.backgroundTintList = tintList
                 mBinding!!.btnVerifyAadhaar.isClickable = true
                 mBinding!!.btnVerifyAadhaar.isEnabled = true*/
            } else {
                aadharNo = aadhaarNumber
                /*val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                mBinding!!.btnVerifyAadhaar.backgroundTintList = tintList
                aadharNo = aadhaarNumber
                mBinding!!.btnVerifyAadhaar.isClickable = false
                mBinding!!.btnVerifyAadhaar.isEnabled = false*/
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }
}