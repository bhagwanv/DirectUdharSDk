package com.sk.directudhar.ui.applyloan

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentBusinessDetailsBinding
import com.sk.directudhar.image.ImageCaptureActivity
import com.sk.directudhar.image.ImageProcessing
import com.sk.directudhar.R
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import com.sk.directudhar.utils.permission.PermissionHandler
import com.sk.directudhar.utils.permission.Permissions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.ArrayList
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
    private var leadMasterId = 0
    private var isGSTVerify = false

    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    private var mBusinessTypeList: ArrayList<BusinessTypeList> = ArrayList()
    private var mBusinessType: ArrayList<String> = ArrayList()
    var mProprietorNameList = ArrayList<AppCompatEditText>()
    var mProprietorNumberList = ArrayList<AppCompatEditText>()
    lateinit var mBusinessDetailsRequestModel: BusinessDetailsRequestModel
    var mBusinessIncorporationDate: String? = null
    var mIncomeSlab: String? = null
    var mOwnershipType: String? = null
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
        leadMasterId = SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
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
            try {
                val mBusinessType: ArrayList<BusinessType> = ArrayList()
                mBusinessType.clear()
                for (i in mProprietorNameList.indices) {
                    mBusinessType.add(
                        BusinessType(
                            mProprietorNameList[i].text.toString(),
                            mProprietorNumberList[i].text.toString()
                        )
                    )
                }
                mBusinessType.forEach {
                    println(it.PartnerName + "    " + it.PartnerNumber)
                }
                var gstNumber = mBinding.etGstNumber.text.toString()
                var businessName = mBinding.etBusinessName.text.toString()
                var businessTurnover = mBinding.etBusinessTurnover.text.toString()
                mBusinessDetailsRequestModel = BusinessDetailsRequestModel(
                    leadMasterId,
                    gstNumber,
                    businessName,
                    mBusinessType,
                    businessTurnover.toInt(),
                    mBusinessIncorporationDate!!,
                    mIncomeSlab!!,
                    mOwnershipType!!
                )
                applyLoanViewModel.validateBusinessDetails(
                    mBusinessDetailsRequestModel,
                    isGSTVerify,
                    false
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        mBinding.llBillUplod.visibility = View.GONE

        mBinding.llCustomerNumber.visibility = View.VISIBLE

        mBinding.etCustomerNumber.addTextChangedListener(aadhaarTextWatcher)

        mBinding.llBillUplod.setOnClickListener {
            askPermission()
        }
        applyLoanViewModel.getGSTDetails(mBinding.etGstNumber.text.toString().trim())
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
        //  val planetsArray = resources.getStringArray(R)

        val businessAdapter =
            ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, mBusinessType)
        mBinding.spBusinessType.adapter = businessAdapter
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
                mIncomeSlab = incomeSlabArray[position]
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
                    mOwnershipType = ownerShipArray[position]
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

        val planetsArray = resources.getStringArray(R.array.service_provider)
        val serviceProviderArrayAdapter =
            ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, planetsArray)
        mBinding.spManualBillUploadType.adapter = serviceProviderArrayAdapter
        mBinding.spManualBillUploadType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    Toast.makeText(
                        activitySDk,
                        "Type" + " " + ownerShipArray[position],
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }
        /* val serviceProviderArrayAdapter =
             ArrayAdapter(activitySDk, R.layout.simple_list_item_1, planetsArray)
         mBinding.spManualBillUploadType.adapter = serviceProviderArrayAdapter
         mBinding.spManualBillUploadType.onItemSelectedListener =
             object : AdapterView.OnItemSelectedListener {
                 override fun onItemSelected(
                     parent: AdapterView<*>,
                     view: View,
                     position: Int,
                     id: Long
                 ) {
                     Toast.makeText(
                         activitySDk,
                         "Type" + " " + ownerShipArray[position],
                         Toast.LENGTH_SHORT
                     ).show()
                 }

                 override fun onNothingSelected(parent: AdapterView<*>) {
                     // Code to perform some action when nothing is selected
                 }
             }*/

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
                mBusinessIncorporationDate = formatDate(year, monthOfYear, dayOfMonth)
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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
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
                applyLoanViewModel.addBusinessDetail(mBusinessDetailsRequestModel)
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
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val data: Intent? = result.data
                    val filePath = data?.getStringExtra(Utils.FILE_PATH).toString()
                    lifecycleScope.launch {
                        val body = ImageProcessing.uploadMultipart(filePath, activitySDk)
                        applyLoanViewModel.electricityDocumentUpload(
                            SharePrefs.getInstance(
                                activitySDk
                            )!!.getInt(SharePrefs.LEAD_MASTERID), body
                        )

                    }
                } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                    Log.d("Result:", "Cancel")
                }
            }


        applyLoanViewModel.electricityDocumentUploadResponse.observe(viewLifecycleOwner) {
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
                    it.data.let {
                        if (it != null) {
                            val model =
                                Gson().fromJson(
                                    it,
                                    ElectricityDocumentUploadResponseModel::class.java
                                )
                            if (model.Result) {
                                mBinding!!.imBillImage.visibility = View.VISIBLE
                                mBinding!!.llDefaultImage.visibility = View.GONE
                                val imageUrl = model.Data
                                Picasso.get().load(imageUrl)
                                    .into(mBinding!!.imBillImage)
                            } else {
                                mBinding!!.imBillImage.visibility = View.GONE
                                mBinding!!.llDefaultImage.visibility = View.VISIBLE
                                activitySDk.toast(model.Msg)
                            }
                        } else {
                            mBinding!!.imBillImage.visibility = View.GONE
                            mBinding!!.llDefaultImage.visibility = View.VISIBLE
                            activitySDk.toast("Image upload failed")

                        }
                    }
                }

            }
        }

        applyLoanViewModel.verifyElectricityBillResponse.observe(viewLifecycleOwner) {
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
                        activitySDk.toast("SuccessFully ${it.Msg}")
                    }
                }
            }
        }


    }


    private fun askPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        val rationale = "Please provide all permission so that you can access the app."
        val options: Permissions.Companion.Options = Permissions.Companion.Options()
            .setRationaleDialogTitle("Allow Permission")
            .setSettingsDialogTitle("Warning")
        Permissions.checkPermissionNew(
            activitySDk,
            permissions,
            rationale,
            options,
            object : PermissionHandler() {
                override fun onGranted() {
                    val tsLong = System.currentTimeMillis() / 1000
                    var fileName = "aadhaarImage_" + tsLong + ".png"
                    val intent = Intent(activitySDk, ImageCaptureActivity::class.java)
                    intent.putExtra(Utils.FILE_NAME, fileName)
                    intent.putExtra(Utils.IS_GALLERY_OPTION, false)
                    resultLauncher?.launch(intent)
                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>) {
                    askPermission()
                }
            })

    }

    private val aadhaarTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not used
        }

        override fun afterTextChanged(s: Editable?) {
            val customerNumber = s.toString().trim()
            if (customerNumber.length < 10) {
                mBinding.imRightIcon.visibility = View.GONE

            } else {
                mBinding.imRightIcon.visibility = View.VISIBLE
            }
        }
    }

}