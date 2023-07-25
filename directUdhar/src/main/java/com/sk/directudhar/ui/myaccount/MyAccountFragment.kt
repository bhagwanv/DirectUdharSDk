package com.sk.directudhar.ui.myaccount

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentMyAccountBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.Utils.Companion.toast
import org.json.JSONObject
import javax.inject.Inject

class MyAccountFragment : Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentMyAccountBinding

    private lateinit var myAccountViewModel: MyAccountViewModel

    private var leadMasterId: Long = 0
    private var accountId: Long = 0
    private var flag: Int = 0  //outstanding=1 and Paid =2 ,  Next Due =3

    @Inject
    lateinit var myAccountFactory: MyAccountFactory

    @Inject
    lateinit var dialog: AppDialogClass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMyAccountBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectMyAccount(this)
        myAccountViewModel =
            ViewModelProvider(this, myAccountFactory)[MyAccountViewModel::class.java]

        //  leadMasterId = SharePrefs.getInstance(activitySDk)?.getInt(SharePrefs.LEAD_MASTERID)!!.toLong()
        leadMasterId = 60286

        mBinding.liOutStanding.setOnClickListener {
            flag = 1
            mBinding.tvTxnDetailTitle.text = "Total Outstanding Txn Details"
            myAccountViewModel.getUdharStatement(accountId, flag)
        }

        mBinding.liNextDue.setOnClickListener {
            flag = 3
            mBinding.tvTxnDetailTitle.text = "Next Due Txn Details"
            myAccountViewModel.getUdharStatement(accountId, flag)
        }

        mBinding.btnUpgrade.setOnClickListener {
            myAccountViewModel.creditLimitRequest(leadMasterId)
        }

        mBinding.btnViewStatement.setOnClickListener {
            val action = MyAccountFragmentDirections.actionMyAccountFragmentToUdharStatementFragment(accountId)
            findNavController().navigate(action)
        }

        myAccountViewModel.myAccountDetailsModelResponse.observe(viewLifecycleOwner) {
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
                    if (it.data != null) {
                        setUI(it.data)
                    } else {
                        activitySDk.toast("Data Not Found")
                    }
                }
            }
        }
        myAccountViewModel.getUdharStatementResponse.observe(viewLifecycleOwner) {
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
                    if (it.data != null && it.data.size > 0) {
                        initRecyclerViewAdapter(it.data)
                    } else {
                        activitySDk.toast("Data Not Found")
                    }
                }
            }
        }
        myAccountViewModel.creditLimitRequestResponse.observe(viewLifecycleOwner) {
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
                    val `object` = JSONObject(it.data.toString())

                    val msg = `object`.getString("Msg")
                    dialog.accountCreatedDialog(activitySDk, msg, "OK")
                    dialog.setOnContinueCancelClick(object : AppDialogClass.OnContinueClicked {
                        override fun onContinueClicked() {

                        }
                    })
                }
            }
        }
        myAccountViewModel.getLoanAccountDetail(leadMasterId)

    }

    private fun initRecyclerViewAdapter(data: ArrayList<UdharStatementModel>) {
        mBinding.liTxnDueDetails.visibility = View.VISIBLE
        mBinding.rvTxnDetails.layoutManager = LinearLayoutManager(activitySDk)
        val adapter = TxnDetailsAdapter(data)
        mBinding.rvTxnDetails.adapter = adapter
    }

    private fun setUI(data: MyAccountDetailsModel) {
        if (data.AccountId != null) {
            // accountId = data.AccountId!!
            accountId = 60089
        }
        if (data.AccountNo != null) {
            mBinding.tvLoanAccountNumber.text = data.AccountNo.toString()
        }

        if (data.TotalUdharLimit != null) {
            mBinding.tvTotalUdhaLimit.text = data.TotalUdharLimit.toString()
        }

        if (data.AvailableUdharLimit != null) {
            mBinding.tvAvailableUdharLimit.text = data.AvailableUdharLimit.toString()
        }

        if (data.Outstanding != null) {
            mBinding.tvTotalOutStanding.text = data.Outstanding.toString()
        }

        if (data.NextDueAmount != null) {
            if (data.NextDueDate != null) {
                mBinding.tvNextDueOn.text =
                    "${data.NextDueAmount.toString()}/n ${data.NextDueDate.toString()}"
            } else {
                mBinding.tvNextDueOn.text = data.NextDueAmount.toString()
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}