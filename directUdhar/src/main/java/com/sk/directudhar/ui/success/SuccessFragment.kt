package com.sk.directudhar.ui.success

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentApprovalPendingBinding
import com.sk.directudhar.databinding.FragmentSuccessBinding
import com.sk.directudhar.ui.approvalpending.ApprovalPendingFactory
import com.sk.directudhar.ui.approvalpending.ApprovalPendingViewModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class SuccessFragment : Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentSuccessBinding

    lateinit var successViewModel: SuccessViewModel

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
        return mBinding.root
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
    }
}