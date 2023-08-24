package com.sk.directudhar.ui.agreement

data class EsignOrAgreementWithOtpOptionResponse(
    val Msg: String,
    val Result: Boolean,
    val Data: Data
)

data class Data(
    val IsEsign: Boolean
)