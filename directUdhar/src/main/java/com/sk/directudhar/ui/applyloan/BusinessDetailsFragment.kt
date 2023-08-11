package com.sk.directudhar.ui.applyloan

import android.R
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.directudhar.databinding.FragmentAadhaarCardBinding
import com.sk.directudhar.databinding.FragmentApplyLoanBinding
import com.sk.directudhar.databinding.FragmentBusinessDetailsBinding
import com.sk.directudhar.ui.applyloan.ApplyLoanFactory
import com.sk.directudhar.ui.applyloan.ApplyLoanViewModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import javax.inject.Inject

class BusinessDetailsFragment : Fragment() {
    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentBusinessDetailsBinding

    lateinit var applyLoanViewModel: ApplyLoanViewModel

    lateinit var proceedBottomDialog: BottomSheetDialog

    @Inject
    lateinit var applyLoanFactory: ApplyLoanFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentBusinessDetailsBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {


    }

    fun businessType(){
        val stateNameList: List<String> = stateList.map { it.StateName }
        val adapter = ArrayAdapter(activitySDk, R.layout.simple_list_item_1, stateNameList)
        mBinding.spState.setAdapter(adapter)
        /*mBinding.spState.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            stateIDValue = stateList[position].Id
            applyLoanViewModel.callCity(stateIDValue)
        }*/

        mBinding.SpCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Toast.makeText(
                    activitySDk,
                    "getString(R.string.selected_item)" + " " + stateList[position],
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
    }
}