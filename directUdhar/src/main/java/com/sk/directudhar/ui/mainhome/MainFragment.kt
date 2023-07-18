package com.sk.directudhar.ui.mainhome

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.sk.directudhar.databinding.FragmentMainBinding
import com.sk.directudhar.ui.MainActivitySDk


class MainFragment : Fragment() {
    private lateinit var mBinding: FragmentMainBinding
    lateinit var activity: MainActivitySDk

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivitySDk
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)
        return mBinding.root
    }









}