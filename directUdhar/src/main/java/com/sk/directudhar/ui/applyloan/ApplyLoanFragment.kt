package com.sk.directudhar.ui.applyloan

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.DialogPocessBinding
import com.sk.directudhar.databinding.FragmentApplyLoanBinding
import com.sk.directudhar.ui.adharcard.AadhaarCardFragmentDirections
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import kotlinx.coroutines.NonDisposableHandle.parent
import javax.inject.Inject



class ApplyLoanFragment : Fragment(), OnClickListener {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentApplyLoanBinding

    lateinit var applyLoanViewModel: ApplyLoanViewModel

    lateinit var proceedBottomDialog: BottomSheetDialog

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
        applyLoanViewModel = ViewModelProvider(this, applyLoanFactory)[ApplyLoanViewModel::class.java]
        applyLoanViewModel.getPersonalInformation(SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID))
        //applyLoanViewModel.callState()
        mBinding.btnNext.setOnClickListener(this)
      //  setupSpinner()
        setupStateAutoComplete()
        setObserber()
    }


    private fun setObserber() {

        applyLoanViewModel.getPersonalInformationResponse.observe(viewLifecycleOwner) {
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
                        mBinding.etFirstName.setText(it.FirstName)
                        mBinding.etLastName.setText(it.LastName)
                        mBinding.etLastName.setText(it.MobileNo)
                        mBinding.etEmailId.setText(it.EmailId)
                        mBinding.etPinCode.setText(it.PinCode)
                        mBinding.etAddress.setText(it.Address)
                    }
                }
            }
        }





        applyLoanViewModel.getLogInResult().observe(activitySDk, Observer { result ->
            if (!result.equals(SuccessType)) {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()


               /* var model=PostCreditBeurauRequestModel()

                applyLoanViewModel.postCreditBeurau(model)*/
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
                       // mBinding.SpCity.setText(" ")
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



    private fun setupStateAutoComplete() {
        val stateNameList: List<String> = stateList.map { it.StateName }
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, stateNameList)
        mBinding.spState.setAdapter(adapter)
        /*mBinding.spState.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            stateIDValue = stateList[position].Id
            applyLoanViewModel.callCity(stateIDValue)
        }*/

        mBinding.SpCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Toast.makeText(activitySDk,
                    "getString(R.string.selected_item)" + " " + stateList[position],
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
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
        /*mBinding.SpCity.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            cityIDValue = cityList[position].Id
            applyLoanViewModel.callCity(cityIDValue)
        }*/

        mBinding.SpCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Toast.makeText(activitySDk,
                    "getString(R.string.selected_item)" + " " + stateList[position],
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnNext -> {
                applyLoanViewModel.performValidation(
                   /* ApplyLoanRequestModel(
                        mBinding.etFirstName.text.toString().trim(),
                        mBinding.etLastName.text.toString().trim(),
                        mBinding.etAlternateNumber.text.toString().trim(),
                        mBinding.etEmailId.text.toString().trim(),
                        mBinding.etPinCode.text.toString().trim(),
                        cityIDValue,
                        stateIDValue,
                        vintageValue,
                        mBinding.cbIsMandate.isChecked
                    )*/
                )

              /*  val action = ApplyLoanFragmentDirections.(
                        )
                findNavController().navigate(action)*/

            }
        }
    }

   /* private fun setupSpinner() {

        val stateNameList: List<String> = stateList.map { it.StateName }
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, stateNameList)
        mBinding.Spinner.setAdapter(adapter)

        mBinding.Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Toast.makeText(activitySDk,
                    "getString(R.string.selected_item)" + " " + stateList[position],
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
    }*/

}