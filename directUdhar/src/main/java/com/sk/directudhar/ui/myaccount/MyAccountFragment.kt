package com.sk.directudhar.ui.myaccount

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentMyAccountBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import javax.inject.Inject

class MyAccountFragment : Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentMyAccountBinding

    private lateinit var myAccountViewModel: MyAccountViewModel

    private var leadMasterId: Int = 0
    private var accountId: Int = 0
    private var flag: Int = 0  //outstanding=1 and Paid =2 ,  Next Due =3

    @Inject
    lateinit var myAccountFactory: MyAccountFactory

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

        // leadMasterId = SharePrefs.getInstance(activitySDk)?.getInt(SharePrefs.LEAD_MASTERID)!!
        leadMasterId = 60286

        myAccountViewModel.myAccountDetailsModelResponse.observe(viewLifecycleOwner) {
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
                        setUI(it.data)
                    } else {
                        Toast.makeText(activitySDk, "Data Not Found", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(activitySDk, it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    if (it.data != null && it.data.size > 0) {
                        initRecyclerViewAdapter(it.data)
                    } else {
                        Toast.makeText(activitySDk, "Data Not Found", Toast.LENGTH_SHORT).show()
                    }
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