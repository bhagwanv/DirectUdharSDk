package com.sk.directudhar.ui.adharcard.aadhaarManullyUpload

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.databinding.FragmentAadhaarManuallyUplaodBinding
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpFactory
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpViewModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import androidx.navigation.fragment.findNavController
import javax.inject.Inject

class AadhaarManuallyUploadFragment : Fragment() {
    @Inject
    lateinit var aadhaarOtpFactory: AadhaarOtpFactory
    private lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentAadhaarManuallyUplaodBinding? = null
    private lateinit var aadhaarOtpViewModel: AadhaarOtpViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (mBinding == null) {
            mBinding = FragmentAadhaarManuallyUplaodBinding.inflate(inflater, container, false)
            initView()
        }
        return mBinding!!.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectAadhaarManuallyUpload(this)
        aadhaarOtpViewModel =
            ViewModelProvider(this, aadhaarOtpFactory)[AadhaarOtpViewModel::class.java]
        setToolBar()

        mBinding!!.btnVerifyAadhaar.setOnClickListener {
            val action =
                AadhaarManuallyUploadFragmentDirections.actionAadhaarManuallyUploadFragmentToKycFailedFragment()
            findNavController().navigate(action)
        }
    }

    private fun setToolBar() {
        activitySDk.ivDateFilterToolbar.visibility = View.GONE
    }
}