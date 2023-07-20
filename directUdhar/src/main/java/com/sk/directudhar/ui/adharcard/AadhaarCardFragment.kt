package com.sk.directudhar.ui.adharcard

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.databinding.FragmentAadhaarCardBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import javax.inject.Inject

class AadhaarCardFragment : Fragment() {

    private lateinit var activitySDk: MainActivitySDk
    private lateinit var mBinding: FragmentAadhaarCardBinding
    lateinit var aadhaarCardViewModel: AadhaarCardViewModel

    @Inject
    lateinit var aadhaarCardFactory: AadhaarCardFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAadhaarCardBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectAadhaarCard(this)
        aadhaarCardViewModel =
            ViewModelProvider(this, aadhaarCardFactory)[AadhaarCardViewModel::class.java]

        mBinding.etAdhaarNumber.addTextChangedListener(aadhaarTextWatcher)

        aadhaarCardViewModel.getAadhaarResult().observe(activitySDk, Observer { result ->
            Toast.makeText(activitySDk, result, Toast.LENGTH_SHORT).show()
        })

        mBinding.btnVerifyAadhaar.setOnClickListener {
            aadhaarCardViewModel.validateAadhaar(mBinding.etAdhaarNumber.text.toString())
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
            val aadhaarNumber = s.toString().trim()
            if (aadhaarNumber.length < 12) {
                mBinding.tilAadhaarNumber.error = "Invalid Aadhaar number"
            } else {
                mBinding.tilAadhaarNumber.error = null
            }
        }
    }
}