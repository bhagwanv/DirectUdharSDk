package com.sk.directudhar.ui.agreement

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentEAgreementOptionsBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject


class EAgreementOptionsFragment : Fragment() {
    lateinit var activitySDk: MainActivitySDk

    private var mBinding: FragmentEAgreementOptionsBinding? = null

    lateinit var eAgreementViewModel: EAgreementViewModel
    private val args: EAgreementOptionsFragmentArgs by navArgs()


    @Inject
    lateinit var eAgreementFactory: EAgreementFactory

    var mobileNo = ""
    var leadMasterId = 0
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
        }
        initView()
        setToolBar()
        return mBinding!!.root
    }


    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "Agreement"
        activitySDk.toolbar.navigationIcon = null
        activitySDk.toolbarBackBtn.visibility = View.VISIBLE
        activitySDk.toolbarBackBtn.setOnClickListener {
            activitySDk.checkSequenceNo(10037)
        }
    }

    override fun onPause() {
        super.onPause()
        activitySDk.toolbarBackBtn.visibility = View.GONE
    }


    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectEAgreementOptions(this)
        eAgreementViewModel =
            ViewModelProvider(this, eAgreementFactory)[EAgreementViewModel::class.java]
        mobileNo = SharePrefs.getInstance(activitySDk)?.getString(SharePrefs.MOBILE_NUMBER)!!
        leadMasterId = SharePrefs.getInstance(
            activitySDk
        )?.getInt(SharePrefs.LEAD_MASTERID)!!
        //mBinding!!.radioOTP.setOnCheckedChangeListener(this)
        // mBinding!!.radioAadhaar.setOnCheckedChangeListener(this)

        mBinding!!.btnNext.setOnClickListener {
            if (verificationType.isEmpty()) {
                activitySDk.toast("Please Select Verification Mode")
            } else if (verificationType == "BY_AADHAAR_VERIFY") {
                eAgreementViewModel.eSignSessionAsync(
                    SignSessionRequestModel(
                        leadMasterId
                    )
                )
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
        eAgreementViewModel.isEsignOrAgreementWithOtp(leadMasterId)
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
                    if (it.data.Result!!) {
                        if (it.data.Data != null) {
                            callWeb(it.data.Data)
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
                        if (it.Data.IsEsign) {
                            verificationType = "BY_AADHAAR_VERIFY"
                            mBinding!!.ivRight.visibility = View.VISIBLE
                            mBinding!!.ivUncheck.visibility = View.GONE
                            mBinding!!.ivUncheckOTP.visibility = View.VISIBLE
                            mBinding!!.ivRightOTP.visibility = View.GONE
                            val tintList =
                                ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                            mBinding!!.btnNext.backgroundTintList = tintList
                            mBinding!!.cvAadhaarEsign.visibility = View.VISIBLE
                            mBinding!!.cvOtpVerification.visibility = View.GONE
                        } else {
                            verificationType = "BY_MOBILE_OTP"
                            mBinding!!.ivRight.visibility = View.GONE
                            mBinding!!.ivUncheck.visibility = View.VISIBLE
                            mBinding!!.ivUncheckOTP.visibility = View.GONE
                            mBinding!!.ivRightOTP.visibility = View.VISIBLE
                            val tintList =
                                ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                            mBinding!!.btnNext.backgroundTintList = tintList
                            mBinding!!.cvAadhaarEsign.visibility = View.GONE
                            mBinding!!.cvOtpVerification.visibility = View.VISIBLE
                        }
                    }

                }
            }
        }
    }

    private fun callWeb(urlString: String) {
        val action =
            EAgreementOptionsFragmentDirections.actionEAgreementOptionsFragmentToESignWebviewFragment(
                urlString
            )
        findNavController().navigate(action)
        // val urlString = "http://mysuperwebsite"
        /*  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          intent.setPackage("com.android.chrome")
          try {
              activitySDk.startActivity(intent)
          } catch (ex: ActivityNotFoundException) {
              // Chrome browser presumably not installed and open Kindle Browser
              intent.setPackage(null)
              activitySDk.startActivity(intent)
          }*/
    }

    /*override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
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
    }*/

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }
}