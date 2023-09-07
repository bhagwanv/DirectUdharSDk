package com.sk.directudhar.ui.personalDetails

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

    private var mBinding: FragmentApplyLoanBinding? = null

    lateinit var applyLoanViewModel: ApplyLoanViewModel

    @Inject
    lateinit var applyLoanFactory: ApplyLoanFactory

    @Inject
    lateinit var dialog: AppDialogClass

    var stateList = mutableListOf<StateModel>()
    var cityList = mutableListOf<CityModel>()


    private var cityIDValue: Int = 0
    private var stateCode: String = ""
    private var stateNameValue: String = ""
    private var cityNameValue: String = ""
    lateinit var model: PostCreditBeurauRequestModel
    private var leadMasterId: Int = 0
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
        val component = DaggerApplicationComponent.builder().build()
        component.injectApplyLoan(this)
        applyLoanViewModel =
            ViewModelProvider(this, applyLoanFactory)[ApplyLoanViewModel::class.java]
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

        applyLoanViewModel.getPersonalInformation(
            SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
        )

        termsAndConditions()

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
                        cityNameValue = it.City
                        stateCode = it.StateCode
                        applyLoanViewModel.callState()
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
                    cityList.clear()
                    ProgressDialog.instance!!.dismiss()
                    cityList.add(CityModel(0, "Select City"))

                    for (data in it.data) {
                        cityList.add(data)
                    }
                    setupCityAutoComplete()

                }
            }
        }

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
                    stateList.clear()
                    ProgressDialog.instance!!.dismiss()
                    stateList.add(StateModel(0, "", "", "", "Select State", "", ""))
                    for (data in it.data) {
                        stateList.add(data)
                    }
                    setupStateAutoComplete()
                }
            }
        }

    }

    fun termsAndConditions() {
        dialog.setOnContinueCancelClick(object : AppDialogClass.OnContinueClicked {
            override fun onContinueClicked(isAgree: Boolean) {
                mBinding!!.cbTermsOfUse.isChecked = isAgree
            }
        })
        val text = SpannableString("By Proceeding, you agree Terms & Conditions.")
        text.setSpan(ForegroundColorSpan(Color.BLUE), 25, 44, 0)
        mBinding!!.tvTermsOfUse.text = text
        mBinding!!.tvTermsOfUse.setOnClickListener {
            if (!activitySDk.privacyPolicyText.isNullOrEmpty()) {
                dialog.termsAndAgreementPopUp(activitySDk, activitySDk.privacyPolicyText)
            } else {
                activitySDk.toast("No Privacy Policy Found")
            }
        }
    }

    private fun setupStateAutoComplete() {
        mBinding!!.rlState.visibility = View.VISIBLE
        mBinding!!.rlCity.visibility = View.VISIBLE
        mBinding!!.llEtState.visibility = View.GONE
        mBinding!!.llEtCity.visibility = View.GONE


        val stateNameList: List<String> = stateList.map { it.StateName }

        mBinding!!.spState.apply {
            this.context ?: return@apply
            this.adapter = object : ArrayAdapter<Any>(
                activitySDk,
                R.layout.simple_custom_list_item,
                stateNameList
            ) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    return super.getDropDownView(position, convertView, parent).also {
                    }
                }

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    return super.getView(position, convertView, parent).apply {
                        setPadding(0, paddingTop, paddingRight, paddingBottom)
                    }
                }
            }
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        (view as TextView).setTextColor(
                            ContextCompat.getColor(
                                activitySDk, R.color.bg_color_gray_variant1
                            )
                        )
                    } else {
                        (view as TextView).setTextColor(
                            ContextCompat.getColor(
                                activitySDk, R.color.text_color_black_variant1
                            )
                        )
                    }
                    stateCode = stateList[position].StateCode
                    stateNameValue = stateList[position].StateName
                    applyLoanViewModel.callCity(stateList[position].Id)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }

        }

        var pos = 0
        for (i in stateList.indices) {
            if (stateCode == stateList[i].StateCode) {
                stateNameValue = stateList[i].StateName
                pos = i
                break
            }
        }
        mBinding!!.spState.setSelection(pos)
    }

    private fun setupCityAutoComplete() {
        val cityNameList: List<String> = cityList.map { it.CityName }
        mBinding!!.spCity.apply {
            this.context ?: return@apply
            this.adapter = object : ArrayAdapter<Any>(
                activitySDk,
                R.layout.simple_custom_list_item,
                cityNameList
            ) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    return super.getDropDownView(position, convertView, parent).also {
                    }
                }

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    return super.getView(position, convertView, parent).apply {
                        setPadding(0, paddingTop, paddingRight, paddingBottom)
                    }
                }
            }
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        (view as TextView).setTextColor(
                            ContextCompat.getColor(
                                activitySDk, R.color.bg_color_gray_variant1
                            )
                        )
                    } else {
                        (view as TextView).setTextColor(
                            ContextCompat.getColor(
                                activitySDk, R.color.text_color_black_variant1
                            )
                        )
                    }
                    cityIDValue = cityList[position].Id
                    cityNameValue = cityList[position].CityName
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }
        }
        var pos = 0
        for (i in cityList.indices) {
            if (cityNameValue == cityList[i].CityName) {
                cityNameValue = cityList[i].CityName
                pos = i
                break
            }
        }
        mBinding!!.spCity.setSelection(pos)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnNext -> {
                // println("NavType>>${args.navType}")
                val firstName = mBinding!!.etFirstName.text.toString()
                val lastName = mBinding!!.etLastName.text.toString()
                val mobileNo = mBinding!!.etAlternateNumber.text.toString()
                var city = mBinding!!.etCity.text.toString()
                val emailId = mBinding!!.etEmailId.text.toString()
                val pinCode = mBinding!!.etPinCode.text.toString()
                val flatNo = mBinding!!.etFlatNo.text.toString()
                val state = stateCode

                model = PostCreditBeurauRequestModel(
                    mobileNo,
                    cityNameValue,
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
                applyLoanViewModel.performValidation(model)
            }
        }
    }

}