package com.sk.directudhar.ui.adharcard.aadhaarManullyUpload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentAadhaarManuallyUplaodBinding
import com.sk.directudhar.image.ImageCaptureActivity
import com.sk.directudhar.image.ImageProcessing
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpFactory
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpViewModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import com.sk.directudhar.utils.permission.PermissionHandler
import com.sk.directudhar.utils.permission.Permissions
import kotlinx.coroutines.launch
import javax.inject.Inject


class AadhaarManuallyUploadFragment : Fragment() {
    @Inject
    lateinit var aadhaarOtpFactory: AadhaarOtpFactory
    private lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentAadhaarManuallyUplaodBinding? = null
    private lateinit var aadhaarOtpViewModel: AadhaarOtpViewModel
    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    private var isUploadAadhaar = false
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
            if (isUploadAadhaar) {
                val action =
                    AadhaarManuallyUploadFragmentDirections.actionAadhaarManuallyUploadFragmentToKycSuccessFragment("ByManuallyUpload")
                findNavController().navigate(action)
            } else {
                val action =
                    AadhaarManuallyUploadFragmentDirections.actionAadhaarManuallyUploadFragmentToKycFailedFragment()
                findNavController().navigate(action)
            }
        }

        mBinding!!.cbTermsOfUse.setOnClickListener {
            if (isUploadAadhaar) {
                if (mBinding!!.cbTermsOfUse.isChecked) {
                    mBinding!!.btnVerifyAadhaar.isClickable = true
                    mBinding!!.btnVerifyAadhaar.isEnabled = true
                    val tintList =
                        ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                    mBinding!!.btnVerifyAadhaar.backgroundTintList = tintList
                } else {
                    mBinding!!.btnVerifyAadhaar.isClickable = false
                    mBinding!!.btnVerifyAadhaar.isEnabled = false
                    val tintList =
                        ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                    mBinding!!.btnVerifyAadhaar.backgroundTintList = tintList
                }
            } else {
                mBinding!!.cbTermsOfUse.isChecked = false
                activitySDk.toast("Please upload aadhaar image")
            }
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
                    val filePath = data!!.getStringExtra(Utils.FILE_PATH).toString()
                    val myBitmap = BitmapFactory.decodeFile(filePath)
                    mBinding!!.imAadhaarImage.setImageBitmap(myBitmap)
                    lifecycleScope.launch {
                        val body = ImageProcessing.uploadMultipart(filePath, activitySDk)
                        aadhaarOtpViewModel.uploadAadhaarImage(
                            body, SharePrefs.getInstance(activitySDk)
                                ?.getInt(SharePrefs.LEAD_MASTERID)!!
                        )
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
                    mBinding!!.imAadhaarImage.visibility = View.GONE
                    mBinding!!.llDefaultImage.visibility = View.VISIBLE
                    activitySDk.toast("Upload failed")
                    isUploadAadhaar = false
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    it.data.let {
                        if (it.Result) {
                            activitySDk.toast(it.Msg)
                            mBinding!!.imAadhaarImage.visibility = View.VISIBLE
                            mBinding!!.llDefaultImage.visibility = View.GONE
                            isUploadAadhaar = true
                            mBinding!!.btnVerifyAadhaar.isClickable = true
                            mBinding!!.btnVerifyAadhaar.isEnabled = true
                        } else {
                            mBinding!!.imAadhaarImage.visibility = View.GONE
                            mBinding!!.llDefaultImage.visibility = View.VISIBLE
                            activitySDk.toast(it.Msg)
                            isUploadAadhaar = false
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