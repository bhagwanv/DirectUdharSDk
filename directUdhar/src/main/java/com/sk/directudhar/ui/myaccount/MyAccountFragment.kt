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
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentMyAccountBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
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

        leadMasterId =
            SharePrefs.getInstance(activitySDk)?.getInt(SharePrefs.LEAD_MASTERID)!!.toLong()

        setToolBar()

        mBinding.liOutStanding.setOnClickListener {
            flag = 1
            mBinding.tvTxnDetailTitle.text = getString(R.string.total_outstanding_txn_details)
            myAccountViewModel.getUdharStatement(accountId, flag)
        }

        mBinding.liNextDue.setOnClickListener {
            flag = 3
            mBinding.tvTxnDetailTitle.text = getString(R.string.next_due_txn_details)
            myAccountViewModel.getUdharStatement(accountId, flag)
        }

        mBinding.btnUpgrade.setOnClickListener {
            myAccountViewModel.creditLimitRequest(leadMasterId)
        }

        mBinding.btnViewStatement.setOnClickListener {
            val action =
                MyAccountFragmentDirections.actionMyAccountFragmentToUdharStatementFragment(
                    accountId
                )
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
                    setUI(it.data)
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
                    if (it.data.size > 0) {
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

    private fun setToolBar() {
        activitySDk.ivDateFilterToolbar.visibility = View.GONE
    }

    private fun initRecyclerViewAdapter(data: ArrayList<UdharStatementModel>) {
        mBinding.liTxnDueDetails.visibility = View.VISIBLE
        mBinding.rvTxnDetails.layoutManager = LinearLayoutManager(activitySDk)
        val adapter = TxnDetailsAdapter(data)
        mBinding.rvTxnDetails.adapter = adapter
    }

    private fun setUI(data: MyAccountDetailsModel) {
        if (data.accountId != null) {
            accountId = data.accountId!!.toLong()
        }
        if (data.accountNo != null) {
            mBinding.tvLoanAccountNumber.text = data.accountNo.toString()
        }
        if (data.totalUdharLimit != null) {
            mBinding.tvTotalUdhaLimit.text = data.totalUdharLimit.toString()
        }
        if (data.availableUdharLimit != null) {
            mBinding.tvAvailableUdharLimit.text = data.availableUdharLimit.toString()
        }
        if (data.outstanding != null) {
            mBinding.tvTotalOutStanding.text = data.outstanding.toString()
        }
        if (data.nextDueAmount != null) {
            if (data.nextDueDate != null) {
                val dueDate = Utils.simpleDateFormate(data.nextDueDate!!,"yyyy-MM-dd'T'HH:mm:ss.SSS","dd MMMM yyyy" )
                val textToShow = "$dueDate \n${data.nextDueAmount}"
                mBinding.tvNextDueOn.text = textToShow
            } else {
                mBinding.tvNextDueOn.text = data.nextDueAmount.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}