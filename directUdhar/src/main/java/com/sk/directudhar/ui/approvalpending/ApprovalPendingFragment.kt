package com.sk.directudhar.ui.approvalpending

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentApprovalPendingBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import okhttp3.internal.UTC
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class ApprovalPendingFragment : Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentApprovalPendingBinding

    lateinit var approvalPendingViewModel: ApprovalPendingViewModel

    @Inject
    lateinit var approvalPendingFactory: ApprovalPendingFactory


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
        mBinding = FragmentApprovalPendingBinding.inflate(inflater, container, false)
        val component = DaggerApplicationComponent.builder().build()
        component.injectApprovalPending(this)
        approvalPendingViewModel =
            ViewModelProvider(this, approvalPendingFactory)[ApprovalPendingViewModel::class.java]
        initView()
        setToolBar()
        return mBinding.root
    }

    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "Approval Pending"
       activitySDk.toolbar.navigationIcon = null
    }

    private fun initView() {
        setObserver()
        mBinding.btContinueShopping.setOnClickListener {
          //  approvalPendingViewModel.updateLeadSuccess(SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID))
            activitySDk.toast("Comming soon")
        }
    }

    private fun setObserver() {
        approvalPendingViewModel.displayDisbursalAmount(
            SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
        )
        approvalPendingViewModel.displayDisbursalAmountResponse.observe(viewLifecycleOwner) {
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
                        mBinding.tvLeadNumber.setText("Lead No.: " + it.data.LeadNo)
                        mBinding.tvCreditLimit.setText("₹ " + it.data.CreditLimit.toString())
                        mBinding.tvProcessingFee.setText("₹ " + it.data.ProcessingFee.toString())
                        mBinding.tvGST.setText("₹ " + it.data.GSTAmount.toString())
                        mBinding.tvDate.setText(it.data.CreatedDate)
                        mBinding.tvDate.setText(
                            "Applied Date: " + Utils.simpleDateFormate(
                                it.data.CreatedDate,
                                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                                "dd MMM yyyy HH:mm a"
                            )
                        )
                        mBinding.tvConvenienceFee.setText("Convenience Fee " + it.data.ConvenionFeeRate.toString() + " % will be Charge on every transaction")
                    }
                }
            }
        }

        approvalPendingViewModel.updateLeadSuccessResponse.observe(viewLifecycleOwner) {
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

                    if (it.data.Result != null) {
                        activitySDk.checkSequenceNo(it.data.Data.SequenceNo)

                    } else {
                        if (it.data.Msg != null) {
                            activitySDk.toast(it.data.Msg)
                        }
                    }
                }
            }
        }
    }
}