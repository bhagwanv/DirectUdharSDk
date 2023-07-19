package com.sk.directudhar.ui.applyloan

import android.R.attr
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentApplyLoanBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import javax.inject.Inject


class ApplyLoanFragment : Fragment(), OnClickListener {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentApplyLoanBinding

    lateinit var applyLoanViewModel: ApplyLoanViewModel

    @Inject
    lateinit var applyLoanFactory: ApplyLoanFactory

    var stateList = mutableListOf<StateModel>()
    var cityList = mutableListOf<CityModel>()
    private var cityIDValue: Int = 0
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
        mBinding = FragmentApplyLoanBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectApplyLoan(this)
        applyLoanViewModel =
            ViewModelProvider(this, applyLoanFactory)[ApplyLoanViewModel::class.java]
        applyLoanViewModel.callState()
        mBinding.btNext.setOnClickListener(this)
        applyLoanViewModel.getLogInResult().observe(activitySDk, Observer { result ->
            Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
        })

        applyLoanViewModel.stateResponse.observe(viewLifecycleOwner) {
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
                    stateList = it.data
                    cityList.clear()
                    mBinding.SpCity.setText(" ")
                    setupStateAutoComplete()

                }
            }
        }
    }

    private fun setupStateAutoComplete() {
        val stateNameList: List<String> = stateList.map { it.StateName }
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, stateNameList)
        mBinding.spState.setAdapter(adapter)
        mBinding.spState.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            stateIDValue=stateList[position].Id
        applyLoanViewModel.callCity(stateIDValue)
        }
        applyLoanViewModel.cityResponse.observe(viewLifecycleOwner) {
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
        mBinding.SpCity.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            cityIDValue = cityList[position].Id
            applyLoanViewModel.callCity(cityIDValue)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btNext -> {
                applyLoanViewModel.performValidation(
                    ApplyLoanRequestModel(
                        mBinding.EtFullName.text.toString().trim(),
                        mBinding.etBusinessName.text.toString().trim(),
                        mBinding.etBusinessAddress.text.toString().trim(),
                        mBinding.etMobileNumber.text.toString().trim(),
                        mBinding.etEmailID.text.toString().trim(),
                        mBinding.etGstNumber.text.toString().trim(),
                        mBinding.EtPincode.text.toString().trim(),
                        mBinding.EtBussniesTurnover.text.toString().trim(),
                        mBinding.EtPincode.text.toString().trim(),cityIDValue,stateIDValue,"",true
                    )
                )
            }
        }
    }

}