package com.sk.directudhar.ui.applyloan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.DialogPocessBinding
import com.sk.directudhar.databinding.FragmentApplyLoanBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject


class ApplyLoanFragment : Fragment(), OnClickListener {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentApplyLoanBinding

    lateinit var applyLoanViewModel: ApplyLoanViewModel

    lateinit var checkoutReasonBottomDialog: BottomSheetDialog

    @Inject
    lateinit var applyLoanFactory: ApplyLoanFactory

    @Inject
    lateinit var dialog: AppDialogClass

    var stateList = mutableListOf<StateModel>()
    var cityList = mutableListOf<CityModel>()


    private var cityIDValue: Int = 0
    private var stateIDValue: Int = 0
    private var vintageValue: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentApplyLoanBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectApplyLoan(this)
        applyLoanViewModel =
            ViewModelProvider(this, applyLoanFactory)[ApplyLoanViewModel::class.java]
        mBinding.tvTeramCondiction.text = Utils.APPLY_LOAN_POLICY
        applyLoanViewModel.callState()
        callBussinessVintage()
        mBinding.btNext.setOnClickListener(this)
        applyLoanViewModel.getLogInResult().observe(activitySDk, Observer { result ->
            if (!result.equals(SuccessType)) {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            }else{
                processDialog()
            }
        })

        applyLoanViewModel.stateResponse.observe(viewLifecycleOwner) {
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
                        stateList = it.data
                        cityList.clear()
                        mBinding.SpCity.setText(" ")
                        setupStateAutoComplete()
                    } else {
                        activitySDk.toast("City not available")
                    }
                }
            }
        }

        applyLoanViewModel.postResponse.observe(viewLifecycleOwner) {
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
                    if (it.data.Result){
                        SharePrefs.getInstance(activitySDk)?.putInt(SharePrefs.LEAD_MASTERID, it.data.Data.LeadMasterId)
                        dialog.accountCreatedDialog(activitySDk,"Congratulations your Account has been created","OK")
                        dialog.setOnContinueCancelClick(object : AppDialogClass.OnContinueClicked {
                            override fun onContinueClicked() {
                                activitySDk.checkSequenceNo(it.data.Data.SequenceNo)
                            }
                        })
                    }else{
                        activitySDk.toast(it.data.Msg)
                    }
                }
            }
        }
    }

    private fun callBussinessVintage() {
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, Utils.vintageList)
        mBinding.SpBussniesVitage.setAdapter(adapter)
        mBinding.SpBussniesVitage.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                vintageValue =  Utils.vintageList[position]
            }
    }



    private fun setupStateAutoComplete() {
        val stateNameList: List<String> = stateList.map { it.StateName }
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, stateNameList)
        mBinding.spState.setAdapter(adapter)
        mBinding.spState.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            stateIDValue = stateList[position].Id
            applyLoanViewModel.callCity(stateIDValue)
        }
        applyLoanViewModel.cityResponse.observe(viewLifecycleOwner) {
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
                    cityList = it.data
                    setupCityAutoComplete()

                }
            }
        }
    }

    private fun setupCityAutoComplete() {
        val cityNameList: List<String> = cityList.map { it.CityName }
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, cityNameList)
        mBinding.SpCity.setAdapter(adapter)
        mBinding.SpCity.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            cityIDValue = cityList[position].Id
            applyLoanViewModel.callCity(cityIDValue)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btNext -> {
                applyLoanViewModel.performValidation(
                    ApplyLoanRequestModel(
                        mBinding.EtFullName.text.toString().trim(),
                        mBinding.etBusinessName.text.toString().trim(),
                        mBinding.etBusinessAddress.text.toString().trim(),
                        mBinding.etMobileNumber.text.toString().trim(),
                        mBinding.etEmailID.text.toString().trim(),
                        mBinding.etGstNumber.text.toString().trim(),
                        mBinding.EtPincode.text.toString().trim(),
                        mBinding.EtBussniesTurnover.text.toString().trim(),
                        mBinding.EtPincode.text.toString().trim(),
                        cityIDValue,
                        stateIDValue,
                        vintageValue,
                        mBinding.cbIsMandate.isChecked
                    )
                )

            }
        }
    }

    private fun processDialog() {
        checkoutReasonBottomDialog = BottomSheetDialog(activitySDk, R.style.Theme_Design_BottomSheetDialog)
        val mDialogCheckOutReasonBinding: DialogPocessBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_pocess, null, false)
        checkoutReasonBottomDialog.setContentView(mDialogCheckOutReasonBinding.root)
        mDialogCheckOutReasonBinding.processPolicy.text=Utils.PROCESS_TEXT
        checkoutReasonBottomDialog.show()

        mDialogCheckOutReasonBinding.btProcess.setOnClickListener {
            checkoutReasonBottomDialog.dismiss()
           applyLoanViewModel.postFromData(ApplyLoanRequestModel(
               mBinding.EtFullName.text.toString().trim(),
               mBinding.etBusinessName.text.toString().trim(),
               mBinding.etBusinessAddress.text.toString().trim(),
               mBinding.etMobileNumber.text.toString().trim(),
               mBinding.etEmailID.text.toString().trim(),
               mBinding.etGstNumber.text.toString().trim(),
               mBinding.EtPincode.text.toString().trim(),
               mBinding.EtBussniesTurnover.text.toString().trim(),
               mBinding.EtPincode.text.toString().trim(),
               cityIDValue,
               stateIDValue,
               vintageValue,
               mBinding.cbIsMandate.isChecked
           ))

        }

        mDialogCheckOutReasonBinding.imClose.setOnClickListener {
            checkoutReasonBottomDialog.dismiss()
        }
    }

}