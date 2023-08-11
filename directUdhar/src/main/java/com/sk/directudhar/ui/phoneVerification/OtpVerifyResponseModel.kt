package com.sk.directudhar.ui.phoneVerification

data class OtpVerifyResponseModel(
     val Msg: String,
     val Result: Boolean,
     val Data: DynamicSequenceNo
)

class DynamicSequenceNo(val LeadMasterId: Int, val SequenceNo: Int) {
}
