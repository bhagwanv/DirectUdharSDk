package com.sk.directudhar.ui.cibilOtpValidate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentPhoneVerificationBinding
import com.sk.directudhar.ui.applyloan.ApplyLoanFragmentArgs
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class CibilPhoneVerificationFragment : Fragment() {
    @Inject
    lateinit var phoneVerificationFactory: CibilPhoneVerificationFactory

    @Inject
    lateinit var dialog: AppDialogClass

    lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentPhoneVerificationBinding? = null
    private lateinit var phoneVerificationViewModel: CibilPhoneVerificationViewModel
    private var accountId: Long = 0
    private var flag: Int = 0  //outstanding=1 and Paid =2 ,  Next Due =3
    private val args: CibilPhoneVerificationFragmentArgs by navArgs()


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
            initView()
        }
        return mBinding!!.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectCibilPhoneVerification(this)
        phoneVerificationViewModel = ViewModelProvider(
            this,
            phoneVerificationFactory
        )[CibilPhoneVerificationViewModel::class.java]

        mBinding!!.etMobileNumber.text = args.mobileNo
        setToolBar()
        setObserver()

        mBinding!!.btnGenOtp.setOnClickListener {
            var mobileNumber = mBinding!!.etMobileNumber.text.toString()
            if (mobileNumber.isNullOrEmpty()) {
                activitySDk.toast("Please enter mobile number")
            } else if (!mBinding!!.cbTermsOfUse.isChecked) {
                activitySDk.toast("Please Check Term and Condition")
            } else {
                phoneVerificationViewModel.callGenOtp(CibilGetOTPRequestModel(SharePrefs.getInstance(activitySDk)
                    ?.getInt(SharePrefs.LEAD_MASTERID)!!,mobileNumber,args.stgOneHitId,args.stgTwoHitId))
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
                              activitySDk.toast(it.Msg)
                            val action =
                                CibilPhoneVerificationFragmentDirections.actionCibilPhoneVerificationFragment(
                                    args.mobileNo,
                                    args.stgOneHitId,
                                    args.stgTwoHitId,
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
        activitySDk.ivDateFilterToolbar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }
}