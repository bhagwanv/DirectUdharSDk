package com.sk.directudhar.ui.agreement

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
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

    private lateinit var mBinding: FragmentEAgreementBinding

    lateinit var eAgreementViewModel: EAgreementViewModel

    @Inject
    lateinit var eAgreementFactory: EAgreementFactory

    var mobileNo=""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEAgreementBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectAgreement(this)
        eAgreementViewModel =
            ViewModelProvider(this, eAgreementFactory)[EAgreementViewModel::class.java]

        eAgreementViewModel.getAgreement(
            SharePrefs.getInstance(activitySDk)!!.getInt(
                SharePrefs.LEAD_MASTERID
            )
        )

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
                        mBinding.tvTermCondition.settings.javaScriptEnabled = true
                        mBinding.tvTermCondition.webViewClient = WebViewClient()
                        mBinding.tvTermCondition.loadDataWithBaseURL(
                            null,
                            it.data.Data,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                }
            }
        }

        mBinding.cbAuthorize.setOnClickListener {
            if(mBinding.cbAuthorize.isChecked){
                mBinding.cbAuthorize.setBackgroundResource(R.drawable.checkbox_checkd_bg)
            }else{
                mBinding.cbAuthorize.setBackgroundResource(R.drawable.checkbox_uncheckd_bg)
            }

        }

        mBinding.btIAgree.setOnClickListener {
            if (mBinding.cbAuthorize.isChecked){

               /* if (mobileNo.isEmpty()) {
                    activitySDk.toast("Please Enter Mobile Number")
                } else {
                    eAgreementViewModel.sendOtp(mobileNo)
                }*/



               var action= EAgreementFragmentDirections.actionEAgreementFragmentToEAgreementOtpFragment(
                    "",
                    mobileNo
                )
                findNavController().navigate(action)
                Log.e("TAG", "initView11: ", )
            }else{
                activitySDk.toast("Please click checkbox to Agree term & Conditions")
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
                    if (it.data.Result!!) {
                        if (it.data.Data!=null){
                            val action =
                                it.data.Data.let { it1 ->
                                    EAgreementFragmentDirections.actionEAgreementFragmentToEAgreementOtpFragment(
                                        it1,
                                        mobileNo
                                    )
                                }
                            findNavController().navigate(action!!)
                        }

                    }
                }
            }
        }

    }
}