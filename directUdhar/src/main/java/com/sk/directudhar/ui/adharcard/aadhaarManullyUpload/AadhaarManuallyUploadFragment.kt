package com.sk.directudhar.ui.adharcard.aadhaarManullyUpload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.sk.directudhar.databinding.FragmentAadhaarManuallyUplaodBinding
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpFactory
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpViewModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.image.ImageCaptureActivity
import com.sk.directudhar.image.ImageProcessing
import com.sk.directudhar.ui.adharcard.AadhaarCardUploadResponseModel
import com.sk.directudhar.ui.pancard.PanCardUplodResponseModel
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import com.sk.directudhar.utils.permission.PermissionHandler
import com.sk.directudhar.utils.permission.Permissions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

class AadhaarManuallyUploadFragment : Fragment() {
    @Inject
    lateinit var aadhaarOtpFactory: AadhaarOtpFactory
    private lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentAadhaarManuallyUplaodBinding? = null
    private lateinit var aadhaarOtpViewModel: AadhaarOtpViewModel
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (mBinding == null) {
            mBinding = FragmentAadhaarManuallyUplaodBinding.inflate(inflater, container, false)
            initView()
        }
        return mBinding!!.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectAadhaarManuallyUpload(this)
        aadhaarOtpViewModel =
            ViewModelProvider(this, aadhaarOtpFactory)[AadhaarOtpViewModel::class.java]
        setToolBar()
        setObserver()
        mBinding!!.btnVerifyAadhaar.setOnClickListener {
            val action =
                AadhaarManuallyUploadFragmentDirections.actionAadhaarManuallyUploadFragmentToKycFailedFragment()
            findNavController().navigate(action)
        }
        mBinding!!.linearLayout.setOnClickListener {
            askPermission()
        }

    }

    private fun setToolBar() {

        activitySDk.ivDateFilterToolbar.visibility = View.GONE
    }

    private fun setObserver() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val data: Intent? = result.data
                    val filePath = data?.getStringExtra(Utils.FILE_PATH).toString()
                    lifecycleScope.launch {
                        val body = ImageProcessing.uploadMultipart(filePath, activitySDk)
                        aadhaarOtpViewModel.uploadAadhaarImage(body)
                    }
                } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                    Log.d("Result:", "Cancel")
                }
            }

        aadhaarOtpViewModel.getUploadImageResponse.observe(viewLifecycleOwner) {
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
                                Gson().fromJson(it, AadhaarCardUploadResponseModel::class.java)
                            if (model.Result) {
                                mBinding!!.imAadhaarImage.visibility = View.VISIBLE
                                mBinding!!.llDefaultImage.visibility = View.GONE
                                val imageUrl = model.Data
                                Picasso.get().load(imageUrl)
                                    .into(mBinding!!.imAadhaarImage)
                            } else {
                                mBinding!!.imAadhaarImage.visibility = View.GONE
                                mBinding!!.llDefaultImage.visibility = View.VISIBLE
                                activitySDk.toast(model.Msg)
                            }
                        } else {
                            mBinding!!.imAadhaarImage.visibility = View.GONE
                            mBinding!!.llDefaultImage.visibility = View.VISIBLE
                            activitySDk.toast("Image upload failed")
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
                Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE)
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
}