package com.sk.directudhar.ui.agreement

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.JsonObject
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentEsignWebviewBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import kotlinx.coroutines.launch
import javax.inject.Inject


class ESignWebviewFragment : Fragment() {
    lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentEsignWebviewBinding? = null
    private val args: ESignWebviewFragmentArgs by navArgs()
    private lateinit var eAgreementViewModel: EAgreementViewModel
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
            mBinding = FragmentEsignWebviewBinding.inflate(inflater, container, false)
            val component = DaggerApplicationComponent.builder().build()
            component.injectESignAgreement(this)
            eAgreementViewModel =
                ViewModelProvider(this, eAgreementFactory)[EAgreementViewModel::class.java]
        }
        initView()
        setToolBar()
        return mBinding!!.root
    }
    private fun setToolBar() {
        activitySDk.toolbar.visibility=View.GONE
        activitySDk.toolbarTitle.text = "Agreement"
        activitySDk.toolbar.navigationIcon = null
    }
    private fun initView() {
        mBinding!!.webview.loadUrl(args.url)
        val webSettings = mBinding!!.webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowContentAccess = true
        webSettings.setSupportMultipleWindows(true)
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.setUserAgentString("Android Mozilla/5.0 AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        mBinding!!.webview.addJavascriptInterface(JavaScriptInterface(
            activitySDk,
             eAgreementViewModel
            ), "Android"
        )
        mBinding!!.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                println("Start>>>>>>>>>>>>>>>>>"+url)
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

        eAgreementViewModel.getESignDocumentsAsyncResponse.observe(viewLifecycleOwner) {
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
                        if (it.get("Result").asBoolean){
                            activitySDk.checkSequenceNo(17)
                        }
                    }
                }
            }
        }
    }
    private class JavaScriptInterface internal constructor(private val activitySDk: MainActivitySDk,private val eAgreementViewModel: EAgreementViewModel) {
        @JavascriptInterface
        fun onSuccess(data: Boolean) {
            activitySDk.lifecycleScope.launch {
                val jsonObject = JsonObject()
                jsonObject.addProperty("LeadMasterId",SharePrefs.getInstance(
                    activitySDk
                )?.getInt(SharePrefs.LEAD_MASTERID)!!)
                eAgreementViewModel.eSignDocumentsAsync(jsonObject)
                activitySDk.checkSequenceNo(17)
            }
            println("onSuccess>>>>>>>>>>>>>>>>>>>>>>>>"+data)
          /*  val action =
                ESignWebviewFragmentDirections.actionESignWebviewFragmentToApprovalPendingFragment()
            findNavController().navigate(action)*/
            //activitySDk.checkSequenceNo(17)
        }
    }

    override fun onDestroy() {
        activitySDk.toolbar.visibility=View.VISIBLE
        super.onDestroy()
        mBinding!!.unbind()
    }
}