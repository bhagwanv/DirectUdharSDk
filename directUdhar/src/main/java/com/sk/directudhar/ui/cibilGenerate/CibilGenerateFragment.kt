package com.sk.directudhar.ui.cibilGenerate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentCibiGenerateBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class CibilGenerateFragment : Fragment() {
    @Inject
    lateinit var cibilGenerateFactory: CibilGenerateFactory

    @Inject
    lateinit var dialog: AppDialogClass

    lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentCibiGenerateBinding? = null
    private lateinit var cibilGenerateViewModel: CibilGenerateViewModel

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
            mBinding = FragmentCibiGenerateBinding.inflate(inflater, container, false)
            val component = DaggerApplicationComponent.builder().build()
            component.injectCibilGenerate(this)
            cibilGenerateViewModel = ViewModelProvider(
                this,
                cibilGenerateFactory
            )[CibilGenerateViewModel::class.java]
        }
        initView()
        return mBinding!!.root
    }

    private fun initView() {
        setToolBar()
        setObserver()
        mBinding!!.btnGenerateCibil.setOnClickListener {
            cibilGenerateViewModel.callGenOtp(SharePrefs.getInstance(activitySDk)
                ?.getInt(SharePrefs.LEAD_MASTERID)!!)
        }
    }

    private fun setObserver() {
        cibilGenerateViewModel.getGenOptResponse.observe(viewLifecycleOwner) {
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
                                CibilGenerateFragmentDirections.actionCibilGenerateFragmentToCibilOtpVerificationFragment(
                                    "",
                                    it.Data.stgOneHitId,
                                    it.Data.stgTwoHitId,
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
        activitySDk.toolbarTitle.text = "Cibil Generate"
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }
}