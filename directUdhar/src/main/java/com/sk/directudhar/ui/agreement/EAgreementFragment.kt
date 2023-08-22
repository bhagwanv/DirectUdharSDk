package com.sk.directudhar.ui.agreement

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentEAgreementBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class EAgreementFragment : Fragment() {
    lateinit var activitySDk: MainActivitySDk

    private var mBinding: FragmentEAgreementBinding? = null

    private lateinit var eAgreementViewModel: EAgreementViewModel
    private var htmlDoc:String = ""

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
            initView()
            setToolBar()
        }
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

        setObserber()

        eAgreementViewModel.getAgreement(
            SharePrefs.getInstance(activitySDk)!!.getInt(
                SharePrefs.LEAD_MASTERID
            )
        )

        mBinding!!.cbTermsOfUse.setOnClickListener {
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

        }

        mBinding!!.btnIAgree.setOnClickListener {
            if (mBinding!!.cbTermsOfUse.isChecked) {
                val action =
                    EAgreementFragmentDirections.actionEAgreementFragmentToEAgreementOptionsFragment(htmlDoc)
                findNavController().navigate(action)
            } else {
                activitySDk.toast("Please click checkbox to Agree term & Conditions")
            }

        }

    }

    private fun setObserber() {
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

                    if (it.data != null) {
                        htmlDoc = it.data.Data!!.Agreementhtml!!
                        mBinding!!.tvTermCondition.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(it.data.Data!!.Agreementhtml, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            Html.fromHtml(it.data.Data!!.Agreementhtml)
                        }
                       /* mBinding!!.tvTermCondition.webViewClient = WebViewClient()
                        mBinding!!.tvTermCondition.loadDataWithBaseURL(
                            null,
                            it.data.Data,
                            "text/html",
                            "UTF-8",
                            null
                        )*/
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