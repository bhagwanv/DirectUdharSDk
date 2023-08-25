package com.sk.directudhar.ui.cibilscore

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentCibilScoreBinding
import com.sk.directudhar.ui.personalDetails.CityModel
import com.sk.directudhar.ui.personalDetails.StateModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import javax.inject.Inject

class CibilScoreFragment : Fragment(), OnClickListener {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentCibilScoreBinding
    lateinit var cibilViewModel: CibilViewModel

    @Inject
    lateinit var cibilViewFactory: CibilViewFactory

    var stateList = mutableListOf<StateModel>()
    var cityList = mutableListOf<CityModel>()
    private var cityIDValue: Int = 0
    private lateinit var cityName: String
    private var stateIDValue: Int = 0
    private var isSkip = false
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
        setToolBar()
        return mBinding.root
    }

    private fun setToolBar() {
        activitySDk.toolbarTitle.text = "CIBIL information"
       activitySDk.toolbar.navigationIcon = null
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectCiBil(this)
        cibilViewModel = ViewModelProvider(this, cibilViewFactory)[CibilViewModel::class.java]
        cibilViewModel.callUserCreditInfo(
            SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
        )
        mBinding.btEmandate.setOnClickListener(this)
        mBinding.tvSkip.setOnClickListener(this)
        /* cibilViewModel.getCiBilResult().observe(activitySDk) { result ->
             if (!result.equals(Utils.SuccessType)) {
                 Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
             } else {
                 processCibil()
             }
         }*/
        /* cibilViewModel.postResponse.observe(viewLifecycleOwner) {
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
         }*/
        /* cibilViewModel.genrateOtpResponse.observe(viewLifecycleOwner) {
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
                     val  action = CibilScoreFragmentDirections.actionCibilScoreFragmentToCiBilOtpFragment(it.data.stgOneHitId,it.data.stgTwoHitId,userInfoModel.MobileNo)
                     findNavController().navigate(action)
                 }
             }
         }*/
        cibilViewModel.getCibilResponse.observe(viewLifecycleOwner) {
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
                            mBinding.name.text = "Hey " + it.Data.FirstName + "!"
                            mBinding.tvCreditLimit.text = "₹" + it.Data.CreditLimit
                            mBinding.tvCreditScore.text = it.Data.CreditScore.toString()
                            mBinding.tvResult.text = "Good"
                            val percent = it.Data.CreditScore / 9
                            mBinding.smc.setPercentWithAnimation(percent)
                        }
                    }
                }
            }
        }

        cibilViewModel.cibilActivityCompleteResponse.observe(viewLifecycleOwner) {
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
                            if (isSkip) {
                                activitySDk.finishAffinity()
                            } else {
                                activitySDk.checkSequenceNo(it.Data.SequenceNo)
                            }
                        }
                    }
                }
            }
        }
    }

    /*   private fun processCibil() {
        cibilViewModel.postFromData(
            CibilResponseModel(mBinding.EtFullName.text.toString().trim(),
                mBinding.etLastName.text.toString().trim(),
                mBinding.etAddress.text.toString().trim(),
                mBinding.etPanNumber.text.toString().trim(),
                mBinding.EtPincode.text.toString().trim(),stateIDValue,cityName,userInfoModel.dateOfBirth,userInfoModel.Gender,userInfoModel.EmailId,userInfoModel.MobileNo,mBinding.cbIsMandate.isChecked,
                SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)))
       }
   */
    /* private fun setupStateAutoComplete() {
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


     }*/

    /*private fun setupCityAutoComplete() {
        val cityNameList: List<String> = cityList.map { it.CityName }
        val adapter = ArrayAdapter(activitySDk, android.R.layout.simple_list_item_1, cityNameList)
        mBinding.SpCity.setAdapter(adapter)
        mBinding.SpCity.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                cityIDValue = cityList[position].Id
                cityName = cityList[position].CityName
                cibilViewModel.callCity(cityIDValue)
            }
    }*/

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btEmandate -> {
                isSkip = false
                cibilViewModel.cibilActivityComplete(
                    SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
                )

            }

            R.id.tvSkip -> {
                isSkip = true
               /* val  action = CibilScoreFragmentDirections.actionCibilScoreFragmentToEAgreementFragment()
                findNavController().navigate(action)*/
                cibilViewModel.cibilActivityComplete(
                    SharePrefs.getInstance(activitySDk)!!.getInt(SharePrefs.LEAD_MASTERID)
                )
            }
        }
    }


}