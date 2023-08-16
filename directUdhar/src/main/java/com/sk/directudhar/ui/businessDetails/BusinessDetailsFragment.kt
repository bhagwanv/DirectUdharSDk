package com.sk.directudhar.ui.businessDetails

import android.Manifest
import android.R.attr
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentBusinessDetailsBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import com.sk.directudhar.utils.permission.PermissionHandler
import com.sk.directudhar.utils.permission.Permissions
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class BusinessDetailsFragment : Fragment() {
    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentBusinessDetailsBinding

    lateinit var businessDetailsViewModel: BusinessDetailsViewModel

    lateinit var proceedBottomDialog: BottomSheetDialog

    @Inject
    lateinit var businessDetailsFactory: BusinessDetailsFactory
    private var leadMasterId = 0
    private var isGSTVerify = false
    private var isVerifyElectricityBill = false

    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    private var mBusinessTypeList: ArrayList<BusinessTypeList> = ArrayList()
    private var mServiceProvider: ArrayList<ServiceProvider> = ArrayList()
    private var mSpiServiceProvider: ArrayList<String> = ArrayList()
    private var mBusinessType: ArrayList<String> = ArrayList()
    var mProprietorNameList = ArrayList<AppCompatEditText>()
    var mProprietorNumberList = ArrayList<AppCompatEditText>()
    lateinit var mBusinessDetailsRequestModel: BusinessDetailsRequestModel
    var mBusinessIncorporationDate=""
    var mIncomeSlab = ""
    var mOwnershipType=""
    var customerNumber=""

    private val BUFFER_SIZE = 1024 * 2
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
        businessDetailsViewModel =
            ViewModelProvider(this, businessDetailsFactory)[BusinessDetailsViewModel::class.java]

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
                    businessDetailsViewModel.getGSTDetails(s.toString())
                } else {
                    isGSTVerify = false
                    mBinding.ivRightGST.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        mBinding.etCustomerNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                   activitySDk.toast("Please Enter Customer Number")
                }else if (s.length == 10) {
                    isVerifyElectricityBill = false
                    customerNumber=s.toString()
                    mBinding.rlServiceProviders.visibility=View.VISIBLE

                } else {
                    isVerifyElectricityBill = false
                    mBinding.ivRightElectrycityBill.visibility = View.GONE
                    mBinding.rlServiceProviders.visibility=View.GONE
                }

            }

            override fun afterTextChanged(s: Editable) {
            }
        })


        mBinding.tvBusinessIncorporationDate.setOnClickListener {
            showDatePicker(mBinding.tvBusinessIncorporationDate)
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
                    businessTurnover,
                    mBusinessIncorporationDate,
                    mIncomeSlab!!,
                    mOwnershipType!!
                )
                businessDetailsViewModel.validateBusinessDetails(
                    mBusinessDetailsRequestModel,
                    isGSTVerify,
                    false,
                    customerNumber,
                    isVerifyElectricityBill,
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        mBinding.llBillUplod.visibility = View.GONE

        mBinding.llCustomerNumber.visibility = View.VISIBLE

       // mBinding.etCustomerNumber.addTextChangedListener(aadhaarTextWatcher)

        mBinding.llBillUplod.setOnClickListener {
            //askPermission()
        }

        businessDetailsViewModel.getGSTDetails(mBinding.etGstNumber.text.toString().trim())
        businessDetailsViewModel.getBusinessTypeList()
        mBinding.tvAddMoreView.setOnClickListener {
            addMoreView(false)
        }


        mBinding.tvUploadStatement.setOnClickListener {
            askPermission1()
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
        mServiceProvider = ServiceProvide.getServiceProvider()
        mServiceProvider.forEach {
            mSpiServiceProvider.add(it.Name)
        }
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

                    if (ownerShipArray[position]=="Owned"){
                        mBinding.rlSpManualBillUploadType.visibility=View.VISIBLE
                    }else{
                        mBinding.rlSpManualBillUploadType.visibility=View.GONE
                        mBinding.llCustomerNumber.visibility=View.GONE
                    }
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
                    Toast.makeText(
                        activitySDk,
                        "Type" + " " + manualBillUploadArray[position],
                        Toast.LENGTH_SHORT
                    ).show()


                    if (manualBillUploadArray[position]=="Customer Number"){
                        mBinding.llCustomerNumber.visibility=View.VISIBLE
                    }else{
                        mBinding.llCustomerNumber.visibility=View.GONE
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }

        val serviceProviderArrayAdapter =
            ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, mSpiServiceProvider)
        mBinding.spServiceProvider.adapter = serviceProviderArrayAdapter
        mBinding.spServiceProvider.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    Toast.makeText(
                        activitySDk,
                        "Type" + " " + mServiceProvider[position].sortName,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("TAG", "onItemSelected: ")
                    var model=BusinessDetailsVerifyElectricityBillRequestModel(SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID),customerNumber,mServiceProvider[position].sortName)
                    businessDetailsViewModel.verifyElectricityBill(model)


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

    private fun showDatePicker(tvDate: TextView) {
        val c = Calendar.getInstance()
        val currentYear = c[Calendar.YEAR]
        val currentMonth = c[Calendar.MONTH]
        val currentDay = c[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { _, year, monthOfYear, dayOfMonth ->
                mBusinessIncorporationDate = formatDate(year, monthOfYear, dayOfMonth)
                //    etDate.setText(selectedDate)
                tvDate.text=
                    StringBuilder() // Month is 0 based so add 1
                        .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/")
                        .append(year).append(" ")


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
        businessDetailsViewModel.getGSTDetailsResponse.observe(viewLifecycleOwner) {
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
        businessDetailsViewModel.getBusinessValidResult().observe(activitySDk) { result ->
            if (result.equals(Utils.BUSINESS_VALIDATE_SUCCESSFULLY)) {
                businessDetailsViewModel.addBusinessDetail(mBusinessDetailsRequestModel)
            } else {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            }
        }
        businessDetailsViewModel.getBusinessDetailsResponse.observe(viewLifecycleOwner) {
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
                            activitySDk.checkSequenceNo(it.DynamicData.SequenceNo)
                            activitySDk.toast("SuccessFully ${it.Msg}")
                        }
                    }
                }
            }
        }
        businessDetailsViewModel.getBusinessTypeListResponse.observe(viewLifecycleOwner) {
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

                if (result.resultCode == AppCompatActivity.RESULT_OK){
                    val data: Intent? = result.data
                    // check condition
                    if (data != null) {
                        var sUri = data.getData ()
                        Log.e("TAG", "sUri$sUri: ")
                      // var sPath= getFilePathFromURI(activitySDk,sUri)
                        // Get PDF path
                       // var sPath = sUri?.getPath ();
                       // Log.e("TAG", "sPath$sPath: ")

                        val fl: File = File(sUri.toString())
                        var path = fl.absolutePath
                        val file: File = File(path)
                        // Parsing any Media type file
                        // Parsing any Media type file
                        val requestBody: RequestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
                        val fileToUpload: MultipartBody.Part = MultipartBody.Part.createFormData("filename", file.name, requestBody)
                        Log.e("TAG", "sPath$path: ")

                        businessDetailsViewModel.bankPassBookUpload(
                            SharePrefs.getInstance(
                                activitySDk
                            )!!.getInt(SharePrefs.LEAD_MASTERID), fileToUpload
                        )
                    }

                }
            }


        businessDetailsViewModel.bankPassBookUploadResponse.observe(viewLifecycleOwner) {
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
                                    BankStateMentUploadResponseModel::class.java
                                )
                            if (model.Result) {

                                Log.e("TAG", "setObserber: 111", )

                            } else {
                                activitySDk.toast(model.Msg)
                            }
                        } else {
                            activitySDk.toast("Image upload failed")

                        }
                    }
                }

            }
        }

        businessDetailsViewModel.verifyElectricityBillResponse.observe(viewLifecycleOwner) {
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
                            isVerifyElectricityBill = true
                            mBinding.ivRightElectrycityBill.visibility = View.VISIBLE
                        } else {
                            mBinding.ivRightElectrycityBill.visibility = View.GONE
                        }
                    }

                }
            }
        }


    }


    private fun selectPDF() {
        // Initialize intent
        Log.e("TAG", "initView4: ")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // Set type

        intent.type = "application/pdf"
        // Launch intent
        resultLauncher!!.launch(intent)

    }

    private fun askPermission1() {
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

                        selectPDF()

                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>) {
                    askPermission1()
                }
            })

    }

    fun getFilePathFromURI(context: Context, contentUri: Uri?): String? {
        //copy file and send new file path
        val fileName = getFileName(contentUri)
        val folderPath = activitySDk.getExternalFilesDir(null)!!.absolutePath + "/SkStatement"
        val file = File(folderPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        return fileName
    }

    fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }
/*
    fun getPDFPath(uri: Uri?): String? {
        val id = DocumentsContract.getDocumentId(uri)
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
        )
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = getContentResolver().query(contentUri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }*/

}