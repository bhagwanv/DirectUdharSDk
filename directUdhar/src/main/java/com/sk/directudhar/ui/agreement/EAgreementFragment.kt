package com.sk.directudhar.ui.agreement

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentEAgreementBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils.Companion.toast
import kotlinx.coroutines.launch
import javax.inject.Inject

class EAgreementFragment : Fragment() {
    @Inject
    lateinit var dialog: AppDialogClass

    lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentEAgreementBinding? = null

    private lateinit var eAgreementViewModel: EAgreementViewModel
    private var htmlDoc: String = ""
    var mobileNo = ""
    var leadMasterId = 0
    var isEsignAadhaar = false

    @Inject
    lateinit var eAgreementFactory: EAgreementFactory

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
            mBinding = FragmentEAgreementBinding.inflate(inflater, container, false)
        }
        initView()
        setToolBar()
        setObserber()
        return mBinding!!.root
    }

    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "Agreement"
        activitySDk.toolbar.navigationIcon = null
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectAgreement(this)
        eAgreementViewModel =
            ViewModelProvider(this, eAgreementFactory)[EAgreementViewModel::class.java]
        mobileNo = SharePrefs.getInstance(activitySDk)?.getString(SharePrefs.MOBILE_NUMBER)!!
        leadMasterId = SharePrefs.getInstance(
            activitySDk
        )?.getInt(SharePrefs.LEAD_MASTERID)!!
        termsAndConditions()
        eAgreementViewModel.getAgreement(
            SharePrefs.getInstance(activitySDk)!!.getInt(
                SharePrefs.LEAD_MASTERID
            )
        )
        eAgreementViewModel.isEsignOrAgreementWithOtp(leadMasterId)
        mBinding!!.cbTermsOfUse.setOnClickListener {
            if (!htmlDoc.isNullOrEmpty()){
                if (mBinding!!.cbTermsOfUse.isChecked) {
                    mBinding!!.btnIAgree.isEnabled = true
                    mBinding!!.btnIAgree.isClickable = true
                    // mBinding!!.cbAuthorize.setBackgroundResource(R.drawable.checkbox_checkd_bg)
                    val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                    mBinding!!.btnIAgree.backgroundTintList = tintList
                } else {
                    mBinding!!.btnIAgree.isEnabled = false
                    mBinding!!.btnIAgree.isClickable = false
                    //  mBinding!!.cbAuthorize.setBackgroundResource(R.drawable.checkbox_uncheckd_bg)
                    val tintList =
                        ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                    mBinding!!.btnIAgree.backgroundTintList = tintList
                }
            }else{
                mBinding!!.cbTermsOfUse.isChecked = false
                dialog.alertDialog(activitySDk,"No Agreement Found For Your Company","Yes")
            }
        }

        mBinding!!.btnIAgree.setOnClickListener {
            if (isEsignAadhaar) {
                eAgreementViewModel.eSignSessionAsync(
                    SignSessionRequestModel(
                        leadMasterId
                    )
                )
            } else {
                if (mobileNo.isEmpty()) {
                    activitySDk.toast("Mobile Number Not Found")
                } else {
                    eAgreementViewModel.sendOtp(mobileNo)
                }
            }
            /* if (mBinding!!.cbTermsOfUse.isChecked) {
                 val action =
                     EAgreementFragmentDirections.actionEAgreementFragmentToEAgreementOptionsFragment(htmlDoc)
                 findNavController().navigate(action)
             } else {
                 activitySDk.toast("Please click checkbox to Agree term & Conditions")
             }*/

        }

    }

    fun termsAndConditions() {
        val text = SpannableString("By Proceeding, you agree agreement.")
        text.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    activitySDk,
                    R.color.text_color_black_variant1
                )
            ), 25, 35, 0
        )
        mBinding!!.tvTermsOfUse.text = text
    }

    private fun setObserber() {
        val webSettings = mBinding!!.webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowContentAccess = true
        webSettings.setSupportMultipleWindows(true)
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.userAgentString = "Android Mozilla/5.0 AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.loadWithOverviewMode = true;
        webSettings.useWideViewPort = true;
        mBinding!!.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                // mBinding!!.pBar.visibility = View.GONE
            }
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                mBinding!!.pBar.visibility = View.GONE
            }
        }
        eAgreementViewModel.agreementResponse.observe(viewLifecycleOwner) {
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
                        if (it.Result) {
                            htmlDoc = it.Data.AgreementfilePath
                            mBinding!!.webview.loadUrl(htmlDoc)
                        } else {
                            dialog.alertDialog(activitySDk, it.Msg, "Yes")
                        }
                    }
                }
            }
        }

        eAgreementViewModel.sendOtpResponse.observe(viewLifecycleOwner) {
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
                        val action =
                            EAgreementFragmentDirections.actionEAgreementFragmentToEAgreementOtpFragment(
                                it.Data!!.TxnNo!!,
                                mobileNo
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }

        eAgreementViewModel.signSessionResponse.observe(viewLifecycleOwner) {
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
                        if (it.Result) {
                            val action =
                                EAgreementFragmentDirections.actionEAgreementFragmentToESignWebviewFragment(
                                    it.Data
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }

        eAgreementViewModel.getEsignOrAgreementWithOtpOptionResponse.observe(viewLifecycleOwner) {
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
                        isEsignAadhaar = it.Data.IsEsign
                        //isEsignAadhaar = true
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }
}