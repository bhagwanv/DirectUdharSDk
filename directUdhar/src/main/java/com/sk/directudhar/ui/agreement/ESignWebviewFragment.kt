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
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.databinding.FragmentEsignWebviewBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import kotlinx.coroutines.launch


class ESignWebviewFragment : Fragment() {
    lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentEsignWebviewBinding? = null
    private val args: ESignWebviewFragmentArgs by navArgs()

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
            initView()
            setToolBar()
        }
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
            activitySDk
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
    }
    private class JavaScriptInterface internal constructor(private val activitySDk: MainActivitySDk,) {
        @JavascriptInterface
        fun onSuccess(data: Boolean) {
            activitySDk.lifecycleScope.launch {
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