package com.sk.directudhar.ui.agreement.agreementOtp

data class AgreementOtpResponseModel(
    var Data: OTPData? = OTPData(),
    var Msg: String? = null,
    var Result: Boolean? = null,
    var DynamicData: String? = null
)

data class OTPData(
    var Data: Data? = Data()
)

data class Data(
    var SequenceNo: Int =0
)