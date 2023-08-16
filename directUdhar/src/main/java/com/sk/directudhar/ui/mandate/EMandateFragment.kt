package com.sk.directudhar.ui.mandate

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentEMandateBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import com.weipl.checkout.WLCheckoutActivity
import org.json.JSONObject
import javax.inject.Inject

class EMandateFragment : Fragment(), OnClickListener, WLCheckoutActivity.PaymentResponseListener {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentEMandateBinding

    lateinit var eMandateViewModel: EMandateViewModel

    @Inject
    lateinit var eMandateFactory: EMandateFactory

    // var bankList = mutableListOf<LiveBank>()


    private var eMandateValue = ""

    // private var bankIDValue = ""
    private var accountType = ""
    private var channelType = ""
    private var leadMasterId = 0


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
        WLCheckoutActivity.setPaymentResponseListener(this)
        WLCheckoutActivity.preloadData(activitySDk)
        initView()
        return mBinding.root
    }

    private fun initView() {
        leadMasterId = SharePrefs.getInstance(activitySDk)
            ?.getInt(SharePrefs.LEAD_MASTERID)!!
        val component = DaggerApplicationComponent.builder().build()
        component.injectEMandate(this)
        eMandateViewModel =
            ViewModelProvider(this, eMandateFactory)[EMandateViewModel::class.java]
        mBinding.btnNext.setOnClickListener(this)
        eMandateViewModel.getEMandateResult().observe(activitySDk, Observer { result ->
            if (!result.equals(Utils.SuccessType)) {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            } else {
                eMandateViewModel.callEmandateAdd(
                    EMandateAddRequestModel(
                        leadMasterId,
                        mBinding.etBankName.text.toString().trim(),
                        mBinding.etIfscCode.text.toString().trim(),
                        accountType,
                        channelType
                    )
                )
            }
        })

        callAccountType()
        callChannelList()

        //eMandateViewModel.callBankList()
        /* eMandateViewModel.bankListResponse.observe(viewLifecycleOwner) {
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
                         setupBank()
                     } else {
                         activitySDk.toast("Bank not available")
                     }
                 }
             }
         }*/

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
                    it.data.let {
                        if (it.Result){
                            val reqJson = JSONObject()
                            //callEmadate(reqJson)
                        }else{
                            activitySDk.toast(it.Msg)
                        }
                    }
                }
            }
        }

    }

    /*  private fun setupBank() {
          val bankNameList: List<String> = bankList.map { it.bankName }
          val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, bankNameList)
          mBinding.spAccountType.setAdapter(adapter)
          mBinding.spAccountType.onItemClickListener =
              AdapterView.OnItemClickListener { parent, view, position, id ->
                  bankIDValue = bankList[position].bankName
              }
      }*/
    private fun callAccountType() {
        val adapter =
            ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, Utils.accountTypeList)
        mBinding.spAccountType.setAdapter(adapter)
        mBinding.spAccountType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                accountType = Utils.accountTypeList[position]
            }
    }

    private fun callChannelList() {
        val adapter =
            ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, Utils.channelList)
        mBinding.spChannelType.adapter = adapter
        mBinding.spChannelType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                channelType = Utils.channelList[position]
            }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnNext -> {
                eMandateViewModel.performValidation(
                    EMandateAddRequestModel(
                        leadMasterId,
                        mBinding.etBankName.text.toString().trim(),
                        mBinding.etIfscCode.text.toString().trim(),
                        accountType,
                        channelType
                    )
                )

            }
        }
    }

    fun callEmadate(reqJson: JSONObject) {
        WLCheckoutActivity.open(activitySDk, reqJson)
    }

    override fun wlCheckoutPaymentResponse(response: JSONObject) {
        Log.d("In wlCheckoutPaymentResponse()", response.toString());
    }

    override fun wlCheckoutPaymentError(response: JSONObject) {
        Log.d("In wlCheckoutPaymentError()", response.toString());
    }
}


