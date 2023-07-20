package com.sk.directudhar.ui.pancard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentPanCardBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
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


class PanCardFragment:Fragment(), View.OnClickListener {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentPanCardBinding

    lateinit var panCardViewModel: PanCardViewModel

    @Inject
    lateinit var panCardFactory: PanCardFactory

    var pickImage: Boolean = false

    private var imageFilePath: String? = null

    private var panUpload: String? = null

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
        mBinding.ivCameraImage.setOnClickListener {

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
            openSomeActivityForResult()
        }

        panCardViewModel.panCardResponse.observe(activitySDk) {
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
                   /* Log.e("TAG", "uploadFilePth:1111 ", )
                    val jsonObject = it.data*/

                    val panCardUplodResponseModel =
                        Gson().fromJson(it.data, PanCardUplodResponseModel::class.java)

                    if (panCardUplodResponseModel.Result) {
                        val ImageUrl= panCardUplodResponseModel.Data

                    } else {
                        activitySDk.toast(panCardUplodResponseModel.Msg)
                    }

                }
            }
        }
    }

    fun openSomeActivityForResult() {
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
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                /*var FILE_PATH = "filePath"
                val filePath = data?.getStringExtra(FILE_PATH).toString()
                val file = File(filePath)
*/

                CoroutineScope(Dispatchers.IO).launch {
                    uploadFilePth()
                }

            }
        }


    private fun imageChooser() {
        pickImage=true
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(i)

    }
    private suspend fun uploadFilePth() {
        val fileToUpload = File(imageFilePath)
        val compressedImageFile = Compressor.compress(activitySDk, fileToUpload)
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), compressedImageFile)
        val body: MultipartBody.Part = createFormData("file",compressedImageFile.name,  requestFile)
        panCardViewModel.uploadPanCard(SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID),body)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btNext -> {
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
       var LEAD_MASTERID= SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
        panUpload = "trip" + LEAD_MASTERID + "image" + ".jpg"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File(Environment.getExternalStorageDirectory().toString() + "/ShopKirana")
        myDir.mkdirs()
        val file = File(storageDir, panUpload)
        imageFilePath = file.absolutePath
        return file
    }
}