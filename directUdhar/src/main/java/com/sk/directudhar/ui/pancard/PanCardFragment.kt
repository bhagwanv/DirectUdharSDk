package com.sk.directudhar.ui.pancard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.DialogChooseImageBinding
import com.sk.directudhar.databinding.DialogPocessBinding
import com.sk.directudhar.databinding.FragmentPanCardBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.SharePrefs.Companion.LEAD_MASTERID
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import java.io.File
import java.security.Permissions

import javax.inject.Inject


class PanCardFragment:Fragment(),OnClickListener {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentPanCardBinding

    lateinit var panCardViewModel: PanCardViewModel

    @Inject
    lateinit var panCardFactory: PanCardFactory

    var pickImage: Boolean = false

    private var imageFilePath: String? = null

    var leadMasterId=0
    var imageUrl=""
    var isAcceptPP= true
    var panNo=""
    var panName=""


    private var panUpload: String? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var imageChooseBottomDialog: BottomSheetDialog

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
        return mBinding.root
    }

    private fun initView() {

        val component = DaggerApplicationComponent.builder().build()
        component.injectPanCard(this)
        panCardViewModel = ViewModelProvider(this, panCardFactory)[PanCardViewModel::class.java]

        leadMasterId= SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
        mBinding.btSubmit.setOnClickListener(this)

        mBinding.rlCameraClick.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    activitySDk,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED
            )
                ActivityCompat.requestPermissions(
                    activitySDk,
                    arrayOf(Manifest.permission.CAMERA),
                    Utils.cameraRequest
                )
            chooseOptionImage()
        }

        panCardViewModel.panCardResponse.observe(activitySDk) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(activitySDk)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    Log.e("TAG", "dismiss: ", )
                    Toast.makeText(activitySDk, it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    val panCardUplodResponseModel =
                        Gson().fromJson(it.data, PanCardUplodResponseModel::class.java)
                    if (panCardUplodResponseModel.Result) {
                        imageUrl= panCardUplodResponseModel.Data
                        Glide.with(activitySDk)
                            .load(imageUrl)
                            .into(mBinding.ivPanCardFrontImage)
                    } else {
                        activitySDk.toast(panCardUplodResponseModel.Msg)
                    }

                }
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap


                CoroutineScope(Dispatchers.IO).launch {
                    uploadFilePth()
                }
            }
        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data

                CoroutineScope(Dispatchers.IO).launch {
                    uploadFilePth()
                }
            }
        }
        panCardViewModel.getLogInResult().observe(activitySDk, Observer { result ->

            if (!result.equals(Utils.SuccessType)) {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            }else{
                if (imageUrl.isNullOrEmpty()){
                    panName=mBinding.etNameAsPAN.text.toString().trim()
                    panNo=mBinding.etPanNumber.text.toString().trim()

                    var model=UpdatePanInfoRequestModel(leadMasterId,panNo,imageUrl,panName,isAcceptPP)
                    panCardViewModel.updatePanInfo(model)
                }

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
                    if (it.data != null) {
                      //  Log.e("TAG", "initView: ${it.data.Msg}", )
                        activitySDk.toast(it.data.Msg)
                    } else {
                       // activitySDk.toast("City not available")
                    }

                }
            }
        }
    }

    fun chooseCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File
        try {
            photoFile = createImageFile()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        val photoUri = FileProvider.getUriForFile(
            (activitySDk)!!,
            requireActivity().packageName + ".provider",
            photoFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        cameraLauncher.launch(intent)
    }
    private fun chooseGallery() {
      //  pickImage=true
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)

        val photoFile: File
        try {
            photoFile = createImageFile()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        val photoUri = FileProvider.getUriForFile(
            (activitySDk)!!,
            requireActivity().packageName + ".provider",
            photoFile
        )

        imagePickerLauncher.launch(intent)

    }
  
    private suspend fun uploadFilePth() {
        val fileToUpload = File(imageFilePath)
        val compressedImageFile = Compressor.compress(activitySDk, fileToUpload)
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), compressedImageFile)
        val body: MultipartBody.Part = createFormData("file",compressedImageFile.name,  requestFile)
        panCardViewModel.uploadPanCard(leadMasterId,body)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btSubmit -> {
                panCardViewModel.performValidation(
                    PanCardRequestModel(
                        mBinding.etNameAsPAN.text.toString().trim(),
                        mBinding.etEmailID.text.toString().trim(),
                        mBinding.etPanNumber.text.toString().trim(),
                        mBinding.etRefrralCode.text.toString().trim(),
                    )
                )
            }
        }
    }

    private fun createImageFile(): File {

        panUpload = "trip" + leadMasterId + "image" + ".jpg"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File(Environment.getExternalStorageDirectory().toString() + "/ShopKirana")
        myDir.mkdirs()
        val file = File(storageDir, panUpload)
        imageFilePath = file.absolutePath
        return file
    }

    fun chooseOptionImage(){
        imageChooseBottomDialog = BottomSheetDialog(activitySDk, R.style.Theme_Design_BottomSheetDialog)
        val mDialogChooseImageBinding: DialogChooseImageBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_choose_image, null, false)
        imageChooseBottomDialog.setContentView(mDialogChooseImageBinding.root)
      //  mDialogChooseImageBinding.processPolicy.text=Utils.PROCESS_TEXT
        imageChooseBottomDialog.show()

        mDialogChooseImageBinding.ivCamera.setOnClickListener {
            chooseCamera()
            imageChooseBottomDialog.dismiss()
        }
        mDialogChooseImageBinding.ivGallery.setOnClickListener {
            chooseGallery()
            imageChooseBottomDialog.dismiss()
        }
        mDialogChooseImageBinding.imClose.setOnClickListener {
            chooseGallery()
            imageChooseBottomDialog.dismiss()
        }
    }
}