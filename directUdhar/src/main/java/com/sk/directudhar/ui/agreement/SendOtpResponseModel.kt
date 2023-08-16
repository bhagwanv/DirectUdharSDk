package com.sk.directudhar.ui.agreement

import com.google.gson.annotations.SerializedName

data class SendOtpResponseModel(
    @SerializedName("Data") var Data: RData? = RData(),
    @SerializedName("Msg") var Msg: String? = null,
    @SerializedName("Result") var Result: Boolean? = null,
    @SerializedName("DynamicData") var DynamicData: String? = null
)

data class RData(
    @SerializedName("TxnNo") var TxnNo: String? = null
)
