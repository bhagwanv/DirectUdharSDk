package com.sk.directudhar.ui.phoneVerification

data class PhoneVerifyResponseModel(
    val Data: Data,
    val Msg: String,
    val Result: Boolean
)
class Data(val TxnNo :String)