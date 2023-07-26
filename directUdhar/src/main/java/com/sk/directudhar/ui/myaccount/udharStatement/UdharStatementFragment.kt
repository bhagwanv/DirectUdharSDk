package com.sk.directudhar.ui.myaccount.udharStatement

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentUdharStatementBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.ui.myaccount.UdharStatementModel
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject

class UdharStatementFragment : Fragment() {

    @Inject
    lateinit var udharStatementFactory: UdharStatementFactory

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentUdharStatementBinding

    private lateinit var udharStatementViewModel: UdharStatementViewModel

    private val args: UdharStatementFragmentArgs by navArgs()

    private var adapter: UdharStatementAdapter = UdharStatementAdapter(ArrayList())


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUdharStatementBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectUdharStatementFragment(this)
        udharStatementViewModel =
            ViewModelProvider(this, udharStatementFactory)[UdharStatementViewModel::class.java]
        bindWidgetsWithAnEvent()
        setupTabLayout()
        initRecyclerView()
        udharStatementViewModel.getUdharStatementResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(activitySDk)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    activitySDk.toast(it.errorMessage)
                    mBinding.rvUdharStatement.visibility = View.GONE
                    mBinding.tvDataNotFound.visibility = View.VISIBLE
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    if (it.data != null) {
                        setRecyclerViewData(it.data)
                    } else {
                        mBinding.rvUdharStatement.visibility = View.GONE
                        mBinding.tvDataNotFound.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        mBinding.rvUdharStatement.layoutManager = LinearLayoutManager(activitySDk)
        adapter = UdharStatementAdapter(ArrayList())
        mBinding.rvUdharStatement.adapter = adapter
    }


    private fun setupTabLayout() {
        mBinding.tabs.addTab(mBinding.tabs.newTab().setText("Outstanding Txn"), true)
        mBinding.tabs.addTab(mBinding.tabs.newTab().setText("Paid Txn"))
    }

    private fun bindWidgetsWithAnEvent() {
        mBinding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                setCurrentTabFragment(p0!!.position)
            }


        })
    }

    private fun setCurrentTabFragment(tabPosition: Int) {
        when (tabPosition) {
            0 -> replaceView(tabPosition)
            1 -> replaceView(tabPosition)
        }
    }

    private fun replaceView(flag: Int) {
        val accountId: Long = args.accountId
        if (flag == 0) {
            udharStatementViewModel.getUdharStatement(accountId, 1)
        } else {
            udharStatementViewModel.getUdharStatement(accountId, 2)
        }
    }

    private fun setRecyclerViewData(data: ArrayList<UdharStatementModel>) {
        adapter = UdharStatementAdapter(data)
        mBinding.rvUdharStatement.adapter = adapter
        if (data.size > 0) {
            mBinding.rvUdharStatement.visibility = View.VISIBLE
            mBinding.tvDataNotFound.visibility = View.GONE
        } else {
            mBinding.rvUdharStatement.visibility = View.GONE
            mBinding.tvDataNotFound.visibility = View.VISIBLE

        }
    }

}