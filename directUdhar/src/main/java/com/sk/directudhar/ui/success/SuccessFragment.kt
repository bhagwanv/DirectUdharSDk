package com.sk.directudhar.ui.success

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.databinding.FragmentSuccessBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.SharePrefs
import javax.inject.Inject

class SuccessFragment : Fragment() {

    lateinit var activitySDk: MainActivitySDk
    private lateinit var mBinding: FragmentSuccessBinding
    lateinit var successViewModel: SuccessViewModel
    private val args: SuccessFragmentArgs by navArgs()

    @Inject
    lateinit var successFactory: SuccessFactory
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSuccessBinding.inflate(inflater, container, false)
        initView()
        setToolBar()
        return mBinding.root
    }
    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "Direct Udhaar"
        activitySDk.toolbar.navigationIcon = null
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectSuccessPending(this)
        successViewModel =
            ViewModelProvider(this, successFactory)[SuccessViewModel::class.java]

        successViewModel.displayDisbursalAmount(
            SharePrefs.getInstance(activitySDk)!!.getInt(
                SharePrefs.LEAD_MASTERID
            )
        )

        mBinding.btnNext.setOnClickListener {
            activitySDk.checkSequenceNo(args.sequenceNo.toInt())
        }
    }
}