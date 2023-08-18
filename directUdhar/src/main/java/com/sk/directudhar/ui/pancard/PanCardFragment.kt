package com.sk.directudhar.ui.pancard

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentPanCardBinding
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
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

class PanCardFragment : Fragment(), OnClickListener {

    @Inject
    lateinit var panCardFactory: PanCardFactory

    lateinit var activitySDk: MainActivitySDk
    private lateinit var mBinding: FragmentPanCardBinding
    lateinit var panCardViewModel: PanCardViewModel
    var imageUrl = ""
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    var panNo: String = ""
    private lateinit var uri: Uri
    private lateinit var galIntent: Intent
    private lateinit var cropIntent: Intent

    val REQUEST_CODE = 200


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
        mBinding.btnVerifyPanCard.setOnClickListener(this)


        mBinding.linearLayout.setOnClickListener {
            askPermission()
        }
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val data: Intent? = result.data
                    val filePath = data!!.getStringExtra(Utils.FILE_PATH).toString()
                    val myBitmap = BitmapFactory.decodeFile(filePath)
                    mBinding.imPanImage.setImageBitmap(myBitmap)
                    lifecycleScope.launch {
                        val body = ImageProcessing.uploadMultipart(filePath, activitySDk)
                        panCardViewModel.uploadPanCard(
                            SharePrefs.getInstance(activitySDk)
                                ?.getInt(SharePrefs.LEAD_MASTERID)!!,
                            body,
                        )
                    }
                } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                    Log.d("Result:", "Cancel")
                }
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
                    it.data.let {
                        if (it.Result) {
                            imageUrl = it.Data.ImageUrl
                            Picasso.get().load(imageUrl)
                                .into(mBinding.imPanImage)
                            activitySDk.toast("Done")
                            mBinding.imPanImage.visibility = View.VISIBLE
                            mBinding.llDefaultImage.visibility = View.GONE
                        } else {
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
                var model = UpdatePanInfoRequestModel(
                    SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID),
                    mBinding.etPanNumber.text.toString().trim(),
                    imageUrl,
                )
                panCardViewModel.updatePanInfo(model)
                mBinding.ivRight.visibility = View.VISIBLE
                mBinding.btnVerifyPanCard.setText("Next")
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
        mBinding!!.etPanNumber.addTextChangedListener(aadhaarTextWatcher)
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

                    selectImageDialog()
                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>) {
                    askPermission()
                }
            })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnVerifyPanCard -> {
                panCardViewModel.performValidation(mBinding.etPanNumber.text.toString())
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
            if (panNumber.length < 10) {
                val tintList =
                    ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                mBinding!!.btnVerifyPanCard.backgroundTintList = tintList
            } else {
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                mBinding!!.btnVerifyPanCard.backgroundTintList = tintList
                panNo = panNumber
            }
        }
    }

    private fun selectImageDialog() {
        val customDialog = Dialog(requireActivity())
        customDialog.setContentView(R.layout.custom_dialog)
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val btnGallery = customDialog.findViewById(R.id.btnGallery) as TextView
        val btnCamera = customDialog.findViewById(R.id.btnCamera) as TextView
        btnGallery.setOnClickListener {
            openGallery()
            customDialog.dismiss()
        }
        btnCamera.setOnClickListener {
            openCamera()
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun openGallery() {
        galIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(
            Intent.createChooser(
                galIntent,
                "Select Image From Gallery "
            ), 2
        )
    }

    private fun openCamera() {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)

    }

    private fun cropImages(uri: Uri) {
        /**set crop image*/
        try {
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri, "image/*")
            cropIntent.putExtra("crop", true)
            cropIntent.putExtra("outputX", 180)
            cropIntent.putExtra("outputY", 180)
            cropIntent.putExtra("aspectX", 3)
            cropIntent.putExtra("aspectY", 4)
            cropIntent.putExtra("scaleUpIfNeeded", true)
            cropIntent.putExtra("return-data", true)
            startActivityForResult(cropIntent, 1)

        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (data != null) {
                uri = data.data!!
                cropImages(uri)
            }
        } else if (requestCode == 1) {
            if (data != null) {
                val bundle = data.extras
                mBinding.imPanImage.visibility = View.VISIBLE
                if (bundle != null) {
                    val bitmap = bundle!!.getParcelable<Bitmap>("data")
                    mBinding!!.imPanImage.setImageBitmap(bitmap)
                } else {
                    val uri = data.data!!
                    mBinding!!.imPanImage.setImageURI(uri)
                }

            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            mBinding!!.imPanImage.setImageBitmap(data.extras!!.get("data") as Bitmap)
            getImageUri(requireContext(), data.extras!!.get("data") as Bitmap)?.let {
                cropImages(it)
            }

        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}