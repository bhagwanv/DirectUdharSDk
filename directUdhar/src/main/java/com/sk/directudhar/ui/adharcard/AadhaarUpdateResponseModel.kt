package com.sk.directudhar.ui.adharcard

import com.google.gson.annotations.SerializedName

data class AadhaarUpdateResponseModel(
    @SerializedName("Data") var Data: String? = null,
    @SerializedName("Msg") var Msg: String? = null,
    @SerializedName("Result") var Result: Boolean? = null,
    @SerializedName("DynamicData") var DynamicData: DynamicData? = DynamicData()
)

data class DynamicData(
    @SerializedName("status") var status: String? = null,
    @SerializedName("statusMsg") var statusMsg: String? = null,
    @SerializedName("msg") var msg: String? = null,
    @SerializedName("data") var data: String? = null,
    @SerializedName("personId") var personId: String? = null,
    @SerializedName("request_id") var requestId: String? = null,
    @SerializedName("error") var error: String? = null
)

