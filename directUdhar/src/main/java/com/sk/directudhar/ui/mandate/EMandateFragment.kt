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
import org.json.JSONArray
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
                        if (it.Result) {
                            println("JSON ::" + it.Data.consumerData)
                            val json = JSONObject(it.Data.consumerData.toString());
                            val reqJson = JSONObject()
                            val jsonFeatures = JSONObject()
                            jsonFeatures.put("enableAbortResponse", true)
                            jsonFeatures.put("enableExpressPay", false)
                            jsonFeatures.put("enableMerTxnDetails", true)
                            jsonFeatures.put("siDetailsAtMerchantEnd", true)
                            jsonFeatures.put("enableSI", true)
                            reqJson.put("features", jsonFeatures)
                            val jsonConsumerData = JSONObject()
                            jsonConsumerData.put(
                                "deviceId",
                                "AndroidSH1"
                            )
                            jsonConsumerData.put(
                                "token",
                                "e1d45b684d49d9c6dfccbab8a6062e9ad6054ec0cc8cf7e1b29b640b7a45fa4c"
                            )
                            jsonConsumerData.put("paymentMode", "netBanking")
                            jsonConsumerData.put(
                                "merchantLogoUrl",
                                "https://images.crunchbase.com/image/upload/c_lpad,h_170,w_170,f_auto,b_white,q_auto:eco,dpr_1/fejsnr7iw4upe286qgpq"
                            )
                            jsonConsumerData.put("merchantId", "T883945")
                            jsonConsumerData.put("currency", "INR")
                            jsonConsumerData.put("consumerId", "consumerId1231")
                            jsonConsumerData.put("consumerMobileNo", "")
                            jsonConsumerData.put("consumerEmailId", "")
                            jsonConsumerData.put("txnId", "T0001")
                            val jArrayItems = JSONArray()
                            val jsonItem1 = JSONObject()
                            jsonItem1.put("itemId", "first")
                            jsonItem1.put("amount", "1")
                            jsonItem1.put("comAmt", "1")
                            jArrayItems.put(jsonItem1)
                            jsonConsumerData.put("items", jArrayItems)
                            val jsonCustomStyle = JSONObject()
                            jsonCustomStyle.put("PRIMARY_COLOR_CODE", "#45beaa")
                            jsonCustomStyle.put("SECONDARY_COLOR_CODE", "#ffffff")
                            jsonCustomStyle.put("BUTTON_COLOR_CODE_1", "#2d8c8c")
                            jsonCustomStyle.put("BUTTON_COLOR_CODE_2", "#ffffff")
                            jsonConsumerData.put("customStyle", jsonCustomStyle)
                            jsonConsumerData.put(
                                "accountType",
                                "Saving"
                            ) //Required for eNACH registration this is mandatory field

                            jsonConsumerData.put("debitStartDate", "11-08-2023")
                            jsonConsumerData.put("debitEndDate", "09-10-2023")
                            jsonConsumerData.put("maxAmount", "1000")
                            jsonConsumerData.put("amountType", "M")
                            jsonConsumerData.put(
                                "frequency",
                                "ADHO"
                            ) //  Available options DAIL, WEEK, MNTH, QURT, MIAN, YEAR, BIMN and ADHO

                            reqJson.put("consumerData", jsonConsumerData)
                            Log.d("jsonConsumerData>>>>>", jsonConsumerData.toString());
                            callEmadate(reqJson)
                        } else {
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
        /*mBinding.spAccountType.setOnItemClickListener(this)*/
        val adapter =
            ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, Utils.accountTypeList)
        mBinding.spAccountType.setAdapter(adapter)
        mBinding.spAccountType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    accountType = Utils.accountTypeList[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }
        /* mBinding.spAccountType.onItemClickListener =
             AdapterView.OnItemClickListener { parent, view, position, id ->
                 accountType = Utils.accountTypeList[position]
             }*/
    }

    private fun callChannelList() {
        val adapter =
            ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, Utils.channelList)
        mBinding.spChannelType.adapter = adapter
        mBinding.spChannelType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    channelType = Utils.channelList[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }
        /* mBinding.spChannelType.onItemClickListener =
             AdapterView.OnItemClickListener { parent, view, position, id ->
                 channelType = Utils.channelList[position]
             }*/
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


