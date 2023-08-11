package com.sk.directudhar.ui.pancard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentPanCardBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import java.io.File
import javax.inject.Inject

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

    var panNo: String = ""

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

        /*panCardViewModel.panCardResponse.observe(activitySDk) {
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
                    val panCardUplodResponseModel =
                        Gson().fromJson(it.data, PanCardUplodResponseModel::class.java)
                    if (panCardUplodResponseModel.Result) {
                        imageUrl = panCardUplodResponseModel.Data
                        Picasso.get().load(panCardUplodResponseModel.Data)
                            .into(mBinding.ivPanCardFrontImage)
                    } else {
                        activitySDk.toast(panCardUplodResponseModel.Msg)
                    }
                }
            }
        }*/



        panCardViewModel.getPanCard().observe(activitySDk, Observer { result ->
            if (!result.equals(Utils.SuccessType)) {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            } else {
                var model = UpdatePanInfoRequestModel(
                    SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID),
                    mBinding.etPanNumber.text.toString().trim(),
                )
                panCardViewModel.updatePanInfo(model)
                mBinding.ivRight.visibility=View.VISIBLE
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
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.bg_color_gray_variant1)
                mBinding!!.btnVerifyPanCard.backgroundTintList = tintList
            } else {
                val tintList = ContextCompat.getColorStateList(activitySDk, R.color.colorPrimary)
                mBinding!!.btnVerifyPanCard.backgroundTintList = tintList
                panNo = panNumber
            }
        }
    }
}