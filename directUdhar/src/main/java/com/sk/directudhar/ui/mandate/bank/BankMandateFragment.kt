package com.sk.directudhar.ui.mandate.bank

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.databinding.FragmentBankMandateBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import javax.inject.Inject

class BankMandateFragment:Fragment() {

    lateinit var activitySDk: MainActivitySDk
    lateinit var mBinding:FragmentBankMandateBinding
    lateinit var  bankMandateViewModel: BankMandateViewModel
    private val args: BankMandateFragmentArgs by navArgs()




    @Inject
    lateinit var bankMandateFactory: BankMandateFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBankMandateBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectBankMandate(this)
        bankMandateViewModel = ViewModelProvider(this, bankMandateFactory)[BankMandateViewModel::class.java]

    }
}