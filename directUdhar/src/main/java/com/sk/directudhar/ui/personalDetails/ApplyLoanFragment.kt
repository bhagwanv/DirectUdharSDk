package com.sk.directudhar.ui.personalDetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentApplyLoanBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils.Companion.SuccessType
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject


class ApplyLoanFragment : Fragment(), OnClickListener {

    lateinit var activitySDk: MainActivitySDk

    private  var mBinding: FragmentApplyLoanBinding?=null

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
    lateinit var model: PostCreditBeurauRequestModel
    private var leadMasterId: Int = 0
    private val args: ApplyLoanFragmentArgs by navArgs()
    private var gender = ""
    private var panNumber = ""
    private var dateOfBirth = ""
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (mBinding == null) {
            mBinding = FragmentApplyLoanBinding.inflate(inflater, container, false)
        }
        initView()
        setToolBar()
        return mBinding!!.root
    }
    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "Personal Details"
        activitySDk.toolbar.navigationIcon = null
    }

    private fun initView() {
        leadMasterId = SharePrefs.getInstance(activitySDk)
            ?.getInt(SharePrefs.LEAD_MASTERID)!!
        val component = DaggerApplicationComponent.builder().build()
        component.injectApplyLoan(this)
        applyLoanViewModel =
            ViewModelProvider(this, applyLoanFactory)[ApplyLoanViewModel::class.java]
        applyLoanViewModel.getPersonalInformation(
            SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
        )

        mBinding!!.btnNext.setOnClickListener(this)
        mBinding!!.cbTermsOfUse.setOnClickListener {
            if (mBinding!!.cbTermsOfUse.isChecked) {
                mBinding!!.btnNext.isClickable = true
                mBinding!!.btnNext.isEnabled = true
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                mBinding!!.btnNext.backgroundTintList = tintList
            } else {
                mBinding!!.btnNext.isClickable = false
                mBinding!!.btnNext.isEnabled = false
                mBinding!!.cbTermsOfUse.isChecked = false
                val tintList =
                    ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                mBinding!!.btnNext.backgroundTintList = tintList
            }

        }
        // setupStateAutoComplete()
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
                    it.data.Data.let {
                        mBinding!!.etFirstName.setText(it.FirstName)
                        mBinding!!.etLastName.setText(it.LastName)
                        mBinding!!.etAlternateNumber.setText(it.MobileNo)
                        mBinding!!.etEmailId.setText(it.EmailId)
                        mBinding!!.etPinCode.setText(it.PinCode)
                        mBinding!!.etFlatNo.setText(it.FlatNo)
                        mBinding!!.etState.setText(it.StateCode)
                        mBinding!!.etCity.setText(it.City)
                        //mBinding!!.etAddress.setText(it.Address)
                        gender = it.Gender
                        panNumber = it.PanNumber
                        dateOfBirth = it.dateOfBirth
                        /* model = PostCreditBeurauRequestModel(
                             it.MobileNo,
                             it.City,
                             it.EmailId,
                             it.FirstName,
                             "",
                             it.Gender,
                             it.LastName,
                             it.LeadMasterId,
                             it.MobileNo,
                             it.PanNumber,
                             it.PinCode,
                             it.StateCode,
                             it.dateOfBirth
                         )*/

                    }
                }
            }
        }

        applyLoanViewModel.getValidResult().observe(activitySDk, Observer { result ->
            if (!result.equals(SuccessType)) {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            } else {
                if (mBinding!!.cbTermsOfUse.isChecked) {
                    applyLoanViewModel.postCreditBeurau(model)
                } else {
                    activitySDk.toast("Checked terms and condition")
                }
            }
        })


        applyLoanViewModel.postCreditBeurauResponse.observe(viewLifecycleOwner) {
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
                            activitySDk.toast(it.Msg)
                            activitySDk.checkSequenceNo(it.DynamicData.SequenceNo)
                        } else {
                            activitySDk.toast(it.Msg)
                        }
                    }
                }
            }
        }

    }


    private fun setupStateAutoComplete() {
        val stateNameList: List<String> = stateList.map { it.StateName }
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, stateNameList)
        mBinding!!.spState.setAdapter(adapter)
        /*mBinding!!.spState.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            stateIDValue = stateList[position].Id
            applyLoanViewModel.callCity(stateIDValue)
        }*/

        mBinding!!.SpCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Toast.makeText(
                    activitySDk,
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
        mBinding!!.SpCity.setAdapter(adapter)
        /*mBinding!!.SpCity.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            cityIDValue = cityList[position].Id
            applyLoanViewModel.callCity(cityIDValue)
        }*/

        mBinding!!.SpCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Toast.makeText(
                    activitySDk,
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
                // println("NavType>>${args.navType}")
                val firstName = mBinding!!.etFirstName.text.toString()
                val lastName = mBinding!!.etLastName.text.toString()
                val mobileNo = mBinding!!.etAlternateNumber.text.toString()
                val city = mBinding!!.etCity.text.toString()
                val emailId = mBinding!!.etEmailId.text.toString()
                val pinCode = mBinding!!.etPinCode.text.toString()
                val flatNo = mBinding!!.etFlatNo.text.toString()
                val state = mBinding!!.etState.text.toString()
                model = PostCreditBeurauRequestModel(
                    mobileNo,
                    city,
                    emailId,
                    firstName,
                    flatNo,
                    gender,
                    lastName,
                    leadMasterId,
                    mobileNo,
                    panNumber,
                    pinCode,
                    state,
                    dateOfBirth
                )
                applyLoanViewModel.performValidation()
            }
        }
    }

}