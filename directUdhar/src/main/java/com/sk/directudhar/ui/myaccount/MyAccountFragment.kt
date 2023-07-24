package com.sk.directudhar.ui.myaccount

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.databinding.FragmentMyAccountBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import javax.inject.Inject

class MyAccountFragment : Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentMyAccountBinding

    lateinit var myAccountViewModel: MyAccountViewModel

    @Inject
    lateinit var myAccountFactory: MyAccountFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMyAccountBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectMyAccount(this)
        myAccountViewModel =
            ViewModelProvider(this, myAccountFactory)[MyAccountViewModel::class.java]

        mBinding.liOutStanding.setOnClickListener {
            mBinding.liTotalOutstandingTxnDetails.visibility = View.VISIBLE
            mBinding.liTxnDueDetails.visibility = View.GONE
        }

        mBinding.liNextDue.setOnClickListener {
            mBinding.liTxnDueDetails.visibility = View.VISIBLE
            mBinding.liTotalOutstandingTxnDetails.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}