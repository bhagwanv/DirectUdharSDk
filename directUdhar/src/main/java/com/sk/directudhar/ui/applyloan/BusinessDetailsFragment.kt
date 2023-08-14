package com.sk.directudhar.ui.applyloan

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentBusinessDetailsBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class BusinessDetailsFragment : Fragment() {
    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentBusinessDetailsBinding

    lateinit var applyLoanViewModel: ApplyLoanViewModel

    lateinit var proceedBottomDialog: BottomSheetDialog

    @Inject
    lateinit var applyLoanFactory: ApplyLoanFactory
    private var leadMasterId =0
    private var isGSTVerify = false
    private var mBusinessTypeList: ArrayList<BusinessTypeList> = ArrayList()
    private var mBusinessType: ArrayList<String> = ArrayList()
    var mProprietorNameList = ArrayList<AppCompatEditText>()
    var mProprietorNumberList = ArrayList<AppCompatEditText>()
    lateinit var mBusinessDetailsRequestModel:BusinessDetailsRequestModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentBusinessDetailsBinding.inflate(inflater, container, false)
        val component = DaggerApplicationComponent.builder().build()
        component.injectBusinessDetails(this)
        applyLoanViewModel =
            ViewModelProvider(this, applyLoanFactory)[ApplyLoanViewModel::class.java]
        applyLoanViewModel.getPersonalInformation(
            SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
        )
        initView()
        spinnerView()
        setObserber()
        return mBinding.root
    }

    private fun initView() {
        leadMasterId =SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
        mBinding.etGstNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 15) {
                    applyLoanViewModel.getGSTDetails(s.toString())
                } else {
                    isGSTVerify = false
                    mBinding.ivRightGST.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        mBinding.etusinessIncorporationDate.setOnClickListener {
            showDatePicker(mBinding.etusinessIncorporationDate)
        }

        mBinding.btnNext.setOnClickListener {
            val mBusinessType: ArrayList<BusinessType> = ArrayList()
            mBusinessType.clear()

            mProprietorNameList.forEach {
                val model =BusinessType()
                println("Name::::" + it.text.toString())
                model.PartnerName = it.text.toString()
                mBusinessType.add(model)
            }
            for (i in mProprietorNumberList.indices) {
                val model =BusinessType()
                model.PartnerNumber = mProprietorNumberList[i].text.toString()
                mBusinessType.set(i,model)
            }

            mBusinessType.forEach {
                println(it.PartnerName+"    "+it.PartnerNumber)
            }

           /* var gstNumber = mBinding.etGstNumber.text.toString()
            var businessName = mBinding.etBusinessName.text.toString()
            mBusinessDetailsRequestModel = BusinessDetailsRequestModel(leadMasterId,gstNumber,businessName)
             applyLoanViewModel.validateBusinessDetails(
                 mBusinessDetailsRequestModel,
                 isGSTVerify,
                 false
             )*/
        }
        applyLoanViewModel.getBusinessTypeList()
        mBinding.tvAddMoreView.setOnClickListener {
            addMoreView(false)
        }
    }


    fun spinnerView() {
        val incomeSlabArray = listOf(
            "1 lac-3 lac",
            "3 lac-5 lac"
        )
        val ownerShipArray = listOf(
            "Rented",
            "Owned",
            "Owned by parents",
            "Owned by Spouse"
        )

        val manualBillUploadArray = listOf(
            "Electricity bill upload",
            "Upload Bill Manual",
            "Customer Number"
        )
        mBinding.spBusinessType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (mBusinessTypeList[position].Id == 1) {
                        mBinding.tvAddMoreView.visibility = View.GONE
                    } else {
                        mBinding.tvAddMoreView.visibility = View.VISIBLE
                    }
                    addMoreView(true)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }

        val incomeAdapter =
            ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, incomeSlabArray)
        mBinding.spIncomeSlab.adapter = incomeAdapter
        mBinding.spIncomeSlab.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                /* Toast.makeText(
                     activitySDk,
                     "Type" + " " + incomeSlabArray[position],
                     Toast.LENGTH_SHORT
                 ).show()*/
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }

        val ownerShipAdapter =
            ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, ownerShipArray)
        mBinding.spOwnerShipType.adapter = ownerShipAdapter
        mBinding.spOwnerShipType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    /*  Toast.makeText(
                          activitySDk,
                          "Type" + " " + ownerShipArray[position],
                          Toast.LENGTH_SHORT
                      ).show()*/
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }

        val manualBillUploadArrayAdapter =
            ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, manualBillUploadArray)
        mBinding.spManualBillUploadType.adapter = manualBillUploadArrayAdapter
        mBinding.spManualBillUploadType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    /* Toast.makeText(
                         activitySDk,
                         "Type" + " " + ownerShipArray[position],
                         Toast.LENGTH_SHORT
                     ).show()*/
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }

    }

    fun addMoreView(isClearView: Boolean) {
        if (isClearView) {
            mProprietorNameList.clear()
            mProprietorNumberList.clear()
            mBinding.llBusinessType.removeAllViews()
        }
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.bottomMargin = 20
        var etName = AppCompatEditText(activitySDk)
        etName.hint = "Proprietor Name"
        etName.background = resources.getDrawable(R.drawable.background_edittext)
        etName.textSize = 12F
        etName.layoutParams = lp
        mProprietorNameList.add(etName)
        val etNumber = AppCompatEditText(activitySDk)
        etNumber.hint = "Number"
        etNumber.background = resources.getDrawable(R.drawable.background_edittext)
        etNumber.textSize = 12F
        etNumber.inputType = InputType.TYPE_CLASS_NUMBER
        val maxLength = 10
        val FilterArray = arrayOfNulls<InputFilter>(1)
        FilterArray[0] = LengthFilter(maxLength)
        etNumber.filters = FilterArray
        etNumber.layoutParams = lp
        mProprietorNumberList.add(etNumber)
        mBinding.llBusinessType.addView(etName)
        mBinding.llBusinessType.addView(etNumber)
    }

    private fun showDatePicker(etDate: EditText) {
        val c = Calendar.getInstance()
        val currentYear = c[Calendar.YEAR]
        val currentMonth = c[Calendar.MONTH]
        val currentDay = c[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = formatDate(year, monthOfYear, dayOfMonth)
                //    etDate.setText(selectedDate)
                etDate.setText(
                    StringBuilder() // Month is 0 based so add 1
                        .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/")
                        .append(year).append(" ")
                )

            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun setObserber() {
        applyLoanViewModel.getGSTDetailsResponse.observe(viewLifecycleOwner) {
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
                            isGSTVerify = true
                            mBinding.ivRightGST.visibility = View.VISIBLE
                        } else {
                            mBinding.ivRightGST.visibility = View.GONE
                        }
                    }

                }
            }
        }
        applyLoanViewModel.getBusinessValidResult().observe(activitySDk) { result ->
            if (result.equals(Utils.AADHAAR_VALIDATE_SUCCESSFULLY)) {
                /* var model = BusinessDetailsRequestModel()
           applyLoanViewModel.addBusinessDetail(model)*/
            } else {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            }
        }
        applyLoanViewModel.getBusinessDetailsResponse.observe(viewLifecycleOwner) {
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
                            activitySDk.toast("SuccessFully ${it.Msg}")
                        }
                    }
                }
            }
        }
        applyLoanViewModel.getBusinessTypeListResponse.observe(viewLifecycleOwner) {
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
                            mBusinessTypeList.clear()
                            mBusinessTypeList.addAll(it.Data)
                            mBusinessTypeList.forEach {
                                mBusinessType.add(it.Name)
                            }
                            val businessAdapter = ArrayAdapter(
                                activitySDk,
                                android.R.layout.simple_list_item_1,
                                mBusinessType
                            )
                            mBinding.spBusinessType.adapter = businessAdapter
                        }
                    }
                }
            }
        }
    }
}