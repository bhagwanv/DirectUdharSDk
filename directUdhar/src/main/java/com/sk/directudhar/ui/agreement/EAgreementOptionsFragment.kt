package com.sk.directudhar.ui.agreement

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.R
import androidx.navigation.fragment.findNavController
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentEAgreementOptionsBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class EAgreementOptionsFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    lateinit var activitySDk: MainActivitySDk

    private var mBinding: FragmentEAgreementOptionsBinding? = null

    lateinit var eAgreementViewModel: EAgreementViewModel

    @Inject
    lateinit var eAgreementFactory: EAgreementFactory

    var mobileNo = ""
    var verificationType = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (mBinding == null) {
            mBinding = FragmentEAgreementOptionsBinding.inflate(inflater, container, false)
            initView()
        }
        return mBinding!!.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectEAgreementOptions(this)
        eAgreementViewModel =
            ViewModelProvider(this, eAgreementFactory)[EAgreementViewModel::class.java]
        mobileNo = SharePrefs.getInstance(activitySDk)?.getString(SharePrefs.MOBILE_NUMBER)!!

        mBinding!!.radioOTP.setOnCheckedChangeListener(this)
        mBinding!!.radioAadhaar.setOnCheckedChangeListener(this)

        mBinding!!.btnNext.setOnClickListener {
            if (verificationType.isEmpty()) {
                activitySDk.toast("Please Select Verification Mode")
            } else if (verificationType == "BY_AADHAAR_VERIFY") {
                activitySDk.toast("call here Aadhaar web")
            } else if (verificationType == "BY_MOBILE_OTP") {
                if (mobileNo.isEmpty()) {
                    activitySDk.toast("Mobile Number Not Found")
                } else {
                    eAgreementViewModel.sendOtp(mobileNo)
                }
            } else {
                activitySDk.toast("Something went wrong!")
            }
        }

        setObserver()
    }

    private fun setObserver() {
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
                        if (it.data.Data != null) {
                            val action =
                                it.data.Data.let { it1 ->
                                    EAgreementOptionsFragmentDirections.actionEAgreementOptionsFragmentToEAgreementOtpFragment(
                                        it1!!.TxnNo!!,
                                        mobileNo
                                    )
                                }
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            if (R.id.radioAadhaar == buttonView!!.id) {
                verificationType = "BY_AADHAAR_VERIFY"
                mBinding!!.radioOTP.isChecked = false
                mBinding!!.ivRight.visibility = View.VISIBLE
                mBinding!!.ivUncheck.visibility = View.GONE
                mBinding!!.ivUncheckOTP.visibility = View.VISIBLE
                mBinding!!.ivRightOTP.visibility = View.GONE
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                mBinding!!.btnNext.backgroundTintList = tintList
            } else {
                verificationType = "BY_MOBILE_OTP"
                mBinding!!.radioAadhaar.isChecked = false
                mBinding!!.ivRight.visibility = View.GONE
                mBinding!!.ivUncheck.visibility = View.VISIBLE
                mBinding!!.ivUncheckOTP.visibility = View.GONE
                mBinding!!.ivRightOTP.visibility = View.VISIBLE
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                mBinding!!.btnNext.backgroundTintList = tintList
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }
}