package com.sk.directudhar.ui.mandate


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentEMandateBinding
import android.view.View.OnClickListener
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.lifecycle.Observer
import com.sk.directudhar.R
import com.sk.directudhar.ui.applyloan.StateModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject
import kotlin.math.E

class EMandateFragment : Fragment(), OnClickListener {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentEMandateBinding

    lateinit var eMandateViewModel: EMandateViewModel

    @Inject
    lateinit var eMandateFactory: EMandateFactory

    var bankList = mutableListOf<LiveBank>()


    private var eMandateValue= ""
    private var bankIDValue = ""
    private var accountTypeIDValue = ""
    private var channelIDValue= ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEMandateBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {

        val component = DaggerApplicationComponent.builder().build()
        component.injectEMandate(this)
        eMandateViewModel =
            ViewModelProvider(this, eMandateFactory)[EMandateViewModel::class.java]

        mBinding.btSetupAutoPay.setOnClickListener(this)

        eMandateViewModel.getEMandateResult().observe(activitySDk, Observer { result ->
            if (!result.equals(Utils.SuccessType)) {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            } else {
                eMandateViewModel.callEmandateAdd(
                    EMandateAddRequestModel(
                        mBinding.etAccountNumber.text.toString().trim(),
                        accountTypeIDValue,
                        bankIDValue,
                        channelIDValue,
                        mBinding.etIfscCode.text.toString().trim(),
                        false,
                        SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID),
                        eMandateValue,
                        "",
                        ""
                    )
                )
            }
        })


        eMandateViewModel.callBankList()
        callAccountType()
        callChannelList()
        eMandateViewModel.bankListResponse.observe(viewLifecycleOwner) {
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
                        bankList = it.data.liveBankList as MutableList<LiveBank>

                        mBinding.spBank.setText(" ")
                        setupBank()
                    } else {
                        activitySDk.toast("Bank not available")
                    }
                }
            }
        }

        eMandateViewModel.eMandateAddResponse.observe(viewLifecycleOwner) {
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
                        if (it.data.request != null) {
                            var data = it.data.error

                        } else {
                           activitySDk.toast(it.data.error!!)
                        }
                    }
                }
            }
        }

    }

    private fun setupBank() {
        val bankNameList: List<String> = bankList.map { it.bankName }
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, bankNameList)
        mBinding.spBank.setAdapter(adapter)
        mBinding.spBank.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                bankIDValue = bankList[position].bankName
            }
    }

    private fun callAccountType() {
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, Utils.accountTypeList)
        mBinding.spAccountType .setAdapter(adapter)
        mBinding.spAccountType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                accountTypeIDValue = Utils.vintageList[position]
            }
    }

    private fun callChannelList() {
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, Utils.channelList)
        mBinding.spChannel.setAdapter(adapter)
        mBinding.spChannel.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                channelIDValue = Utils.vintageList[position]
            }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btSetupAutoPay -> {
                eMandateViewModel.performValidation(
                    EMandateAddRequestModel(
                        mBinding.etAccountNumber.text.toString().trim(),
                        accountTypeIDValue,
                        bankIDValue,
                        channelIDValue,
                        mBinding.etIfscCode.text.toString().trim(),
                        false,
                        SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID),
                        eMandateValue,
                        "",
                        ""
                    )
                )

            }
        }
    }

}


