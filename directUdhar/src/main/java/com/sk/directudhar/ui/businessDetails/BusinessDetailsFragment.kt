package com.sk.directudhar.ui.businessDetails

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextUtils
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentBusinessDetailsBinding
import com.sk.directudhar.image.ImageCaptureActivity
import com.sk.directudhar.image.ImageProcessing
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import com.sk.directudhar.utils.permission.PermissionHandler
import com.sk.directudhar.utils.permission.Permissions
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
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
    var mBusinessIncorporationDate = ""
    var mIncomeSlab = ""
    var mOwnershipType = ""
    var customerNumber = ""
    var serviceProvideName = ""
    var businessTypeId = 0
    var uploadType = ""
    var uploadBillImage = ""
    var isUploadBillImage = false
    var isCustomerNumber = false
    var uploadBankPassBookImage = ""
    var isPanVerify = false
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
        setToolBar()
        return mBinding.root
    }

    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "Business Details"
        activitySDk.toolbar.navigationIcon = null
        activitySDk.toolbarBackBtn.visibility = View.VISIBLE
        activitySDk.toolbarBackBtn.setOnClickListener{
            activitySDk.checkSequenceNo(14)
        }
    }

    override fun onPause() {
        super.onPause()
        activitySDk.toolbarBackBtn.visibility = View.GONE
    }

    private fun initView() {
        leadMasterId = SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
        mBinding.etGstNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (Utils.isValidGSTNo(s.toString())) {
                    mBinding.ivRightGST.visibility = View.VISIBLE
                    businessDetailsViewModel.getGSTDetails(s.toString())
                } else {
                    isGSTVerify = false
                    mBinding.ivRightGST.visibility = View.GONE
                    mBinding.tvBusinessIncorporationDate.isEnabled = true
                    mBinding.tvBusinessIncorporationDate.isClickable = true
                    mBinding.spBusinessType.isEnabled = true
                    mBinding.spBusinessType.isClickable = true
                    mBinding.tvBusinessIncorporationDate.text = ""
                    mBinding.etBusinessName.setText("")
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        mBinding.btnVerifyBill.setOnClickListener {
            if (mBinding.etCustomerNumber.text.toString().isNullOrEmpty()) {
                activitySDk.toast("Please Enter Customer Number")
            } else if (serviceProvideName.isNullOrEmpty() || serviceProvideName == "None") {
                activitySDk.toast("Please select Service Provide name")
            } else {
                val model = BusinessDetailsVerifyElectricityBillRequestModel(
                    SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID),
                    mBinding.etCustomerNumber.text.toString(),
                    serviceProvideName
                )
                businessDetailsViewModel.verifyElectricityBill(model)
            }
        }

        mBinding.tvBusinessIncorporationDate.setOnClickListener {
            showDatePicker(mBinding.tvBusinessIncorporationDate)
        }

        mBinding.btnNext.setOnClickListener {
            try {
                /*val mBusinessType: ArrayList<BusinessType> = ArrayList()
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
                }*/
                val sBusinessTypeName = mBinding.etBusinessTypeName.text.toString()
                val sBusinessTypePanNumber = mBinding.etBusinessTypePanNumber.text.toString()
                val mBusinessTypeList: ArrayList<BusinessType> = ArrayList()
                mBusinessTypeList.add(BusinessType(sBusinessTypeName, sBusinessTypePanNumber))
                val gstNumber = mBinding.etGstNumber.text.toString()
                val businessName = mBinding.etBusinessName.text.toString()
                val businessTurnover = mBinding.etBusinessTurnover.text.toString()
                if (isCustomerNumber) {
                    if (isVerifyElectricityBill) {
                        customerNumber = mBinding.etCustomerNumber.text.toString()
                    }
                } else {
                    if (!isUploadBillImage) {
                        uploadBillImage = ""
                    }
                }
                mBusinessDetailsRequestModel = BusinessDetailsRequestModel(
                    leadMasterId,
                    gstNumber,
                    businessTypeId,
                    businessName,
                    mBusinessTypeList,
                    businessTurnover,
                    mBusinessIncorporationDate,
                    mIncomeSlab,
                    mOwnershipType,
                    customerNumber,
                    uploadBillImage,
                    uploadBankPassBookImage
                )
                businessDetailsViewModel.validateBusinessDetails(
                    mBusinessDetailsRequestModel,
                    isGSTVerify,
                    false,
                    customerNumber,
                    isVerifyElectricityBill,
                    isPanVerify,
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        businessDetailsViewModel.getBusinessTypeList()
        /* mBinding.tvAddMoreView.setOnClickListener {
             addMoreView(false)
         }*/
        mBinding.llUploadBillManual.setOnClickListener {
            uploadType = "bill"
            askPermission()
        }
        mBinding.tvUploadStatement.setOnClickListener {
            uploadType = "statement"
            askPermission()
        }
        mBinding.etBusinessTypePanNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val panNumber = s.toString().trim()
                if (Utils.isValidPanCardNo(panNumber)) {
                    businessDetailsViewModel.panVerification(leadMasterId, panNumber)
                    // mBinding.ivPanVerifyRight.visibility = View.VISIBLE
                } else {
                    mBinding.ivPanVerifyRight.visibility = View.GONE
                }
            }
        })
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
        /* "Electricity bill upload",
         "Upload Bill Manual",*/
        val manualBillUploadArray = listOf("Customer Number", "Upload Bill Manual")
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
                    businessTypeId = mBusinessTypeList[position].Id
                    mBinding.rlPanNumber.visibility = View.VISIBLE
                    when (businessTypeId) {
                        1 -> {
                            mBinding.etBusinessTypeName.hint = "Proprietor Name"
                            mBinding.rlPanNumber.visibility = View.GONE
                        }
                        2 -> {
                            mBinding.etBusinessTypeName.hint = "Partner Name"
                            mBinding.rlPanNumber.visibility = View.GONE
                        }
                        3 -> mBinding.etBusinessTypeName.hint = "Partner Name"
                        4 -> mBinding.etBusinessTypeName.hint = "Chairman Name"
                        5 -> mBinding.etBusinessTypeName.hint = "Director Name"
                        6 -> mBinding.etBusinessTypeName.hint = "Karta Name"
                        else -> mBinding.etBusinessTypeName.hint = "Name"
                    }
                    mBinding.llBusinessType.visibility = View.VISIBLE
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

                    if (ownerShipArray[position] == "Owned") {
                        mBinding.rlSpManualBillUploadType.visibility = View.VISIBLE
                    } else {
                        mBinding.rlSpManualBillUploadType.visibility = View.GONE
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
                    if (manualBillUploadArray[position] == "Customer Number") {
                        isCustomerNumber = true
                        mBinding.llCustomerNumber.visibility = View.VISIBLE
                        mBinding.llUploadBillManual.visibility = View.GONE
                    } else {
                        isCustomerNumber = false
                        mBinding.llCustomerNumber.visibility = View.GONE
                        mBinding.llUploadBillManual.visibility = View.VISIBLE
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
                    Log.e("TAG", "onItemSelected: ")
                    serviceProvideName = mServiceProvider[position].sortName

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
                tvDate.text =
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

    @RequiresApi(Build.VERSION_CODES.Q)
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
                            mBinding.etBusinessName.setText(it.Data.BusinessName)
                            if (!it.Data.BusinessType.isNullOrEmpty()) {
                                for (position in mBusinessType.indices) {
                                    if (mBusinessTypeList[position].Name.toLowerCase() == it.Data.BusinessType.toLowerCase()) {
                                        mBinding.spBusinessType.setSelection(position)
                                        mBinding.spBusinessType.isEnabled = false
                                        mBinding.spBusinessType.isClickable = false
                                        break
                                    }
                                }
                            }
                            if (!it.Data.BusinessIncDate.isNullOrEmpty()) {
                                mBinding.tvBusinessIncorporationDate.isEnabled = false
                                mBinding.tvBusinessIncorporationDate.isClickable = false
                                mBinding.tvBusinessIncorporationDate.text = it.Data.BusinessIncDate
                                mBusinessIncorporationDate = Utils.getDateFormat(
                                    it.Data.BusinessIncDate,
                                    "yyyy-MM-dd'T'HH:mm:ss"
                                )!!
                                println("mBusinessIncorporationDate>>"+mBusinessIncorporationDate)
                            }
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
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (uploadType == "bill") {
                        val filePath = data!!.getStringExtra(Utils.FILE_PATH).toString()
                        val myBitmap = BitmapFactory.decodeFile(filePath)
                        mBinding.imBillImage.setImageBitmap(myBitmap)
                        lifecycleScope.launch {
                            ProgressDialog.instance!!.show(activitySDk)
                            val body = ImageProcessing.uploadMultipart(filePath, activitySDk)
                            businessDetailsViewModel.uploadBillManual(
                                body, SharePrefs.getInstance(activitySDk)
                                    ?.getInt(SharePrefs.LEAD_MASTERID)!!
                            )
                        }
                    } else {
                        data?.data.let {
                            val path = getFilePathFromURI(it)
                            lifecycleScope.launch {
                                val compressedImageFile =
                                    Compressor.compress(activitySDk, File(path)) {}
                                val requestFile: RequestBody =
                                    compressedImageFile.asRequestBody("*/*".toMediaTypeOrNull())
                                val body: MultipartBody.Part =
                                    MultipartBody.Part.createFormData(
                                        "file",
                                        compressedImageFile.name,
                                        requestFile
                                    )
                                businessDetailsViewModel.bankPassBookUpload(
                                    SharePrefs.getInstance(
                                        activitySDk
                                    )!!.getInt(SharePrefs.LEAD_MASTERID), body
                                )
                            }
                        }
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
                        if (it.Result) {
                            uploadBankPassBookImage = it.Data.ImageUrl
                            mBinding.rlSuccessfullyUpload.visibility = View.VISIBLE
                            mBinding.rlUploadStatement.visibility = View.GONE
                            activitySDk.toast(it.Msg)
                        } else {
                            mBinding.rlSuccessfullyUpload.visibility = View.GONE
                            mBinding.rlUploadStatement.visibility = View.VISIBLE
                            activitySDk.toast(it.Msg)
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
                            if (!it.Data.consumer_name.isNullOrEmpty()) {
                                mBinding.tvCustomerName.visibility = View.VISIBLE
                                mBinding.tvCustomerName.text = it.Data.consumer_name
                            }
                            mBinding.ivRightElectrycityBill.visibility = View.VISIBLE
                        } else {
                            isVerifyElectricityBill = false
                            mBinding.ivRightElectrycityBill.visibility = View.GONE
                        }
                    }

                }
            }
        }

        businessDetailsViewModel.getUploadBillResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(activitySDk)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    mBinding.imBillImage.visibility = View.GONE
                    mBinding.llDefaultImage.visibility = View.VISIBLE
                    activitySDk.toast("Upload failed")
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    it.data.let {
                        if (it.Result) {
                            isUploadBillImage = true
                            uploadBillImage = it.Data.ImageUrl
                            // activitySDk.toast(it.Msg)
                            mBinding.imBillImage.visibility = View.VISIBLE
                            mBinding.llDefaultImage.visibility = View.GONE
                        } else {
                            isUploadBillImage = false
                            uploadBillImage = ""
                            mBinding.imBillImage.visibility = View.GONE
                            mBinding.llDefaultImage.visibility = View.VISIBLE
                            activitySDk.toast(it.Msg)
                        }
                    }

                }
            }
        }

        businessDetailsViewModel.getPanVerificationResponse.observe(viewLifecycleOwner) {
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
                            isPanVerify = true
                            mBinding.ivPanVerifyRight.visibility = View.VISIBLE
                        } else {
                            isPanVerify = false
                            mBinding.ivPanVerifyRight.visibility = View.GONE
                        }
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
                    if (uploadType == "bill") {
                        val tsLong = System.currentTimeMillis() / 1000
                        val fileName = "aadhaarImage_" + tsLong + ".png"
                        val intent = Intent(activitySDk, ImageCaptureActivity::class.java)
                        intent.putExtra(Utils.FILE_NAME, fileName)
                        intent.putExtra(Utils.IS_GALLERY_OPTION, false)
                        resultLauncher?.launch(intent)
                    } else {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "application/pdf"
                        resultLauncher!!.launch(intent)
                    }
                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>) {
                    askPermission()
                }
            })

    }


    fun getFilePathFromURI(contentUri: Uri?): String? {
        val tsLong = System.currentTimeMillis() / 1000
        val fileName = "bank_statement$tsLong.pdf"
        val folderPath = activitySDk.getExternalFilesDir(null)!!.absolutePath + "/SkBankStatement"
        val file = File(folderPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile = File(file.toString() + File.separator + fileName)
            // create folder if not exists
            copy(activitySDk, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
        try {
            val inputStream = context.contentResolver.openInputStream(srcUri!!) ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            copystream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Throws(java.lang.Exception::class, IOException::class)
    fun copystream(input: InputStream?, output: OutputStream?): Int {
        val buffer = ByteArray(BUFFER_SIZE)
        val `in` = BufferedInputStream(input, BUFFER_SIZE)
        val out = BufferedOutputStream(output, BUFFER_SIZE)
        var count = 0
        var n = 0
        try {
            while (`in`.read(buffer, 0, BUFFER_SIZE).also { n = it } != -1) {
                out.write(buffer, 0, n)
                count += n
            }
            out.flush()
        } finally {
            try {
                out.close()
            } catch (e: IOException) {
                Log.e(e.message, e.toString())
            }
            try {
                `in`.close()
            } catch (e: IOException) {
                Log.e(e.message, e.toString())
            }
        }
        return count
    }
}