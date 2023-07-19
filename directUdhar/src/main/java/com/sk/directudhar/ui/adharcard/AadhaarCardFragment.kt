package com.sk.directudhar.ui.adharcard

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sk.directudhar.databinding.FragmentAadhaarCardBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import kotlin.math.ceil

class AadhaarCardFragment : Fragment() {

    private lateinit var activitySDk: MainActivitySDk
    private lateinit var mBinding: FragmentAadhaarCardBinding

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
        mBinding.etAdhaarNumber.addTextChangedListener(aadhaarTextWatcher)

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
            if (isValidAadhar(aadhaarNumber)) {
                mBinding.tilAadhaarNumber.error = null
            } else {
                mBinding.tilAadhaarNumber.error = "Invalid Aadhar number"
            }
        }
    }

    fun isValidAadhar(aadharNumber: String): Boolean {
        // Remove any spaces or special characters from the Aadhar number
        val cleanAadharNumber = aadharNumber.replace("\\s".toRegex(), "")

        // Check if Aadhar number is exactly 12 digits after removing spaces
        if (cleanAadharNumber.length != 12) {
            return false
        }

        // Check if Aadhar number contains only numeric characters
        if (!cleanAadharNumber.matches("\\d+".toRegex())) {
            return false
        }

        // Validate the first digit (should be between 1 to 9)
        val firstDigit = cleanAadharNumber[0].toString().toInt()
        if (firstDigit < 1 || firstDigit > 9) {
            return false
        }

        // Check the checksum (last digit)
        val lastDigit = cleanAadharNumber[11].toString().toInt()
        if (getAadharChecksum(cleanAadharNumber) != lastDigit) {
            return false
        }

        return true
    }

    private fun getAadharChecksum(aadharNumber: String): Int {
        var sum = 0

        for (i in 0 until aadharNumber.length - 1) {
            val digit = aadharNumber[i].toString().toInt()

            // Alternate digits are multiplied by 2
            sum += if (i % 2 == 0) {
                if (digit * 2 > 9) {
                    digit * 2 - 9
                } else {
                    digit * 2
                }
            } else {
                digit
            }
        }

        // Find the next multiple of 10 and subtract the sum to get the checksum
        return (ceil(sum.toDouble() / 10) * 10 - sum).toInt()
    }
}