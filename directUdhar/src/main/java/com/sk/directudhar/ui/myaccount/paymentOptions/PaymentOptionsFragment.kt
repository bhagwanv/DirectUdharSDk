package com.sk.directudhar.ui.myaccount.paymentOptions

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.databinding.FragmentPaymentOptionsBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.ui.myaccount.udharStatement.UdharStatementFactory
import javax.inject.Inject

class PaymentOptionsFragment : Fragment() {

    private val args:PaymentOptionsFragmentArgs by navArgs()
    @Inject
    lateinit var udharStatementFactory: UdharStatementFactory

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentPaymentOptionsBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPaymentOptionsBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        Log.i("TAG", "initView: ${args.transactionId}")

    }

}