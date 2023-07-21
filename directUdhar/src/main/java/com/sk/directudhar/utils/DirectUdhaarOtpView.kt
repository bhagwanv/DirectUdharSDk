package com.sk.directudhar.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import android.widget.LinearLayout
import com.sk.directudhar.R

class DirectUdhaarOtpView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var otpDigits: Array<EditText?> = arrayOfNulls(6)

    init {
        inflate(context, R.layout.direct_udhaar_otp_view, this)
        setupViews()
    }

    private fun setupViews() {
        otpDigits[0] = findViewById(R.id.otpDigit1)
        otpDigits[1] = findViewById(R.id.otpDigit2)
        otpDigits[2] = findViewById(R.id.otpDigit3)
        otpDigits[3] = findViewById(R.id.otpDigit4)
        otpDigits[4] = findViewById(R.id.otpDigit5)
        otpDigits[5] = findViewById(R.id.otpDigit6)

        setupTextWatchers()
        //   setupFocusChangeListeners()
    }

    private fun setupTextWatchers() {
        for (i in otpDigits.indices) {
            otpDigits[i]?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < otpDigits.size - 1) {
                        otpDigits[i + 1]?.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            otpDigits[i]?.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (i > 0) {
                        otpDigits[i - 1]?.requestFocus()
                        otpDigits[i - 1]?.setText("")
                    }
                }
                false
            }
        }
    }

    fun getOTP(): String {
        val sb = StringBuilder()
        for (digit in otpDigits) {
            sb.append(digit?.text.toString())
        }
        return sb.toString()
    }

    fun clearOTP() {
        for (digit in otpDigits) {
            digit?.text?.clear()
        }
        otpDigits[0]?.requestFocus()
    }
}