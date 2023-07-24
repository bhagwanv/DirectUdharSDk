package com.sk.directudhar.ui.cibilscore

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentCibilScoreBinding
import com.sk.directudhar.ui.applyloan.CityModel
import com.sk.directudhar.ui.applyloan.StateModel
import com.sk.directudhar.ui.cibilscore.cibiotp.GenrateOtpModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class CibilScoreFragment : Fragment(),OnClickListener {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentCibilScoreBinding
    lateinit var cibilViewModel: CibilViewModel
    @Inject
    lateinit var cibilViewFactory: CibilViewFactory
    lateinit var userInfoModel: CibilRequestModel

    var stateList = mutableListOf<StateModel>()
    var cityList = mutableListOf<CityModel>()
    private var cityIDValue: Int = 0
    private lateinit var cityName: String
    private var stateIDValue: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCibilScoreBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectCiBil(this)
        cibilViewModel = ViewModelProvider(this, cibilViewFactory)[CibilViewModel::class.java]
        cibilViewModel.callUserInfo(SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID))
        cibilViewModel.callState()
        cibilViewModel.stateResponse.observe(viewLifecycleOwner) {
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
                    if (it.data != null && it.data.size > 0) {
                        stateList = it.data
                        cityList.clear()
                        mBinding.SpCity.setText(" ")
                        setupStateAutoComplete()
                    } else {
                        activitySDk.toast("City not available")
                    }
                }
            }
        }
        mBinding.btProcess.setOnClickListener(this)
        cibilViewModel.getCiBilResult().observe(activitySDk) { result ->
            if (!result.equals(Utils.SuccessType)) {
                Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
            } else {
                processCibil()
            }
        }
        cibilViewModel.postResponse.observe(viewLifecycleOwner) {
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
                    cibilViewModel.postFromData(GenrateOtpModel(userInfoModel.MobileNo,it.data.stgOneHitId,it.data.stgTwoHitId))
                }
            }
        }
        cibilViewModel.genrateOtpResponse.observe(viewLifecycleOwner) {
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
                    findNavController().navigate(R.id.actionCibilScoreFragmentToCiBilOtpFragment)
                }
            }
        }
        cibilViewModel.userResponse.observe(viewLifecycleOwner) {
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
                    userInfoModel= it.data
                    userInfoData(userInfoModel)


                }
            }
        }

    }

    private fun userInfoData(data: CibilRequestModel) {
        mBinding.EtFullName.setText(data.FirstName)
        mBinding.etLastName.setText(data.LastName)
        mBinding.etAddress.setText(data.FlatNo)
        mBinding.etPanNumber.setText(data.PanNumber)
        mBinding.EtPincode.setText(data.PinCode)
    }

    private fun processCibil() {
     cibilViewModel.postFromData(
         CibilRequestModel(mBinding.EtFullName.text.toString().trim(),
             mBinding.etLastName.text.toString().trim(),
             mBinding.etAddress.text.toString().trim(),
             mBinding.etPanNumber.text.toString().trim(),
             mBinding.EtPincode.text.toString().trim(),stateIDValue,cityName,userInfoModel.dateOfBirth,userInfoModel.Gender,userInfoModel.EmailId,userInfoModel.MobileNo,mBinding.cbIsMandate.isChecked,
             SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)))
    }

    private fun setupStateAutoComplete() {
        val stateNameList: List<String> = stateList.map { it.StateName }
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, stateNameList)
        mBinding.spState.setAdapter(adapter)
        mBinding.spState.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                stateIDValue = stateList[position].Id
                cibilViewModel.callCity(stateIDValue)
            }
        cibilViewModel.cityResponse.observe(viewLifecycleOwner) {
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
                    cityList = it.data
                    setupCityAutoComplete()

                }
            }
        }


    }

    private fun setupCityAutoComplete() {
        val cityNameList: List<String> = cityList.map { it.CityName }
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, cityNameList)
        mBinding.SpCity.setAdapter(adapter)
        mBinding.SpCity.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                cityIDValue = cityList[position].Id
                cityName = cityList[position].CityName
                cibilViewModel.callCity(cityIDValue)
            }
    }

    override fun onClick(v: View) {
        when (v.id) {
           R.id.btProcess -> {
                cibilViewModel.performValidation(CibilRequestModel(mBinding.EtFullName.text.toString().trim(),
                    mBinding.etLastName.text.toString().trim(),
                    mBinding.etAddress.text.toString().trim(),
                    mBinding.etPanNumber.text.toString().trim(),
                    mBinding.EtPincode.text.toString().trim(),stateIDValue,cityName,userInfoModel.dateOfBirth,userInfoModel.Gender,userInfoModel.EmailId,userInfoModel.MobileNo,mBinding.cbIsMandate.isChecked,
                    SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)))

            }
        }
    }


}