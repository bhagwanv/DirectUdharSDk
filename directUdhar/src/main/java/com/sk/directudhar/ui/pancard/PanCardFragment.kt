package com.sk.directudhar.ui.pancard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.sk.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentPanCardBinding
import com.sk.directudhar.image.ImageProcessing
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import com.sk.directudhar.utils.permission.PermissionHandler
import com.sk.directudhar.utils.permission.Permissions
import com.squareup.picasso.Picasso
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.launch

class PanCardFragment : Fragment(), OnClickListener {

    @Inject
    lateinit var panCardFactory: PanCardFactory

    lateinit var activitySDk: MainActivitySDk
    private lateinit var mBinding: FragmentPanCardBinding
    lateinit var panCardViewModel: PanCardViewModel
    var pickImage: Boolean = false
    lateinit var galleryFilePath: File
    private var imageFilePath: String? = null
    var imageUrl = ""
    private var panUpload: String? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var imageChooseBottomDialog: BottomSheetDialog
    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    private var startForProfileImageResult: ActivityResultLauncher<Intent>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPanCardBinding.inflate(inflater, container, false)
        initView()
        setToolBar()
        return mBinding.root
    }
    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "Pan Verification"
        activitySDk.toolbar.navigationIcon = null
    }
    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectPanCard(this)
        panCardViewModel = ViewModelProvider(this, panCardFactory)[PanCardViewModel::class.java]
        setObserber()
        mBinding.btnVerifyPanCard.setOnClickListener(this)

        mBinding.linearLayout.setOnClickListener {
            askPermission()
        }
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    ProgressDialog.instance!!.show(activitySDk)
                    val data: Intent? = result.data
                    val filePath = data!!.getStringExtra(Utils.FILE_PATH).toString()
                    val myBitmap = BitmapFactory.decodeFile(filePath)
                    mBinding.imPanImage.setImageBitmap(myBitmap)
                    /*lifecycleScope.launch {
                        val body = ImageProcessing.uploadMultipart(filePath, activitySDk)
                        panCardViewModel.uploadPanCard(
                            SharePrefs.getInstance(activitySDk)
                                ?.getInt(SharePrefs.LEAD_MASTERID)!!,
                            body,
                        )
                    }*/
                } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                    Log.d("Result:", "Cancel")
                }
            }

        startForProfileImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    mBinding.imPanImage.visibility = View.VISIBLE
                    mBinding.imPanImage.setImageURI(fileUri)

                    lifecycleScope.launch {
                        val body = ImageProcessing.uploadMultipart(data?.data!!.path!!, activitySDk)
                        panCardViewModel.uploadPanCard(
                            SharePrefs.getInstance(activitySDk)
                                ?.getInt(SharePrefs.LEAD_MASTERID)!!,
                            body,
                        )
                    }
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    activitySDk.toast("${ImagePicker.getError(data)}")
                } else {
                    activitySDk.toast("Task Cancelled")
                }
            }

        mBinding.etPanNumber.addTextChangedListener(aadhaarTextWatcher)
    }

    private fun setObserber() {
        panCardViewModel.panCardImageUploadResponse.observe(activitySDk) {
            when (it) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    Toast.makeText(activitySDk, it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    it.data.let {
                        if (it.Result) {
                            imageUrl = it.Data.ImageUrl
                            Picasso.get().load(imageUrl)
                                .into(mBinding.imPanImage)
                            activitySDk.toast(it.Msg)
                            mBinding.imPanImage.visibility = View.VISIBLE
                            mBinding.llDefaultImage.visibility = View.GONE
                        } else {
                            imageUrl = ""
                            activitySDk.toast(it.Msg)
                            mBinding.imPanImage.visibility = View.VISIBLE
                            mBinding.llDefaultImage.visibility = View.GONE
                        }
                    }
                }
            }
        }

        panCardViewModel.getPanCard().observe(activitySDk, Observer { result ->
            if (!result.equals(Utils.SuccessType)) {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            } else {
                val model = UpdatePanInfoRequestModel(
                    SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID),
                    mBinding.etPanNumber.text.toString().trim(),
                    mBinding.etFatherName.text.toString().trim(),
                    imageUrl
                )
                panCardViewModel.updatePanInfo(model)
            }
        })

        panCardViewModel.updatePanInfoResponse.observe(activitySDk) {
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
                    if (it.data.Result) {
                        activitySDk.checkSequenceNo(it.data.Data.SequenceNo)
                        activitySDk.toast(it.data.Msg)
                    } else {
                        activitySDk.toast(it.data.Msg)
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
                    /*val tsLong = System.currentTimeMillis() / 1000
                    var fileName = "panImage_" + tsLong + ".png"
                    val intent = Intent(activitySDk, ImageCaptureActivity::class.java)
                    intent.putExtra(Utils.FILE_NAME, fileName)
                    intent.putExtra(Utils.IS_GALLERY_OPTION, true)
                    resultLauncher?.launch(intent)*/
                    ImagePicker.with(activitySDk)
                        .crop()
                        .compress(1024)         //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(
                            1080,
                            1080
                        )  //Final image resolution will be less than 1080 x 1080(Optional)
                        .createIntent { intent ->
                            startForProfileImageResult!!.launch(intent)
                        }

                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>) {
                    askPermission()
                }
            })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnVerifyPanCard -> {
                panCardViewModel.performValidation(mBinding.etPanNumber.text.toString(),imageUrl, mBinding.etFatherName.text.toString())
            }
        }
    }

    private val aadhaarTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not used
        }

        override fun afterTextChanged(s: Editable?) {
            val panNumber = s.toString().trim()
            if (Utils.isValidPanCardNo(panNumber)) {
                mBinding.ivRight.visibility = View.VISIBLE
            } else {
                mBinding.ivRight.visibility = View.GONE
            }
        }
    }
}