package com.sk.directudhar.ui.agreement

import com.google.gson.annotations.SerializedName

data class AgreementResponseModel(
    @SerializedName("Data") var Data: AgreementData? = AgreementData(),
    @SerializedName("Msg") var Msg: String? = null,
    @SerializedName("Result") var Result: Boolean? = null,
    @SerializedName("DynamicData") var DynamicData: String? = null

)

data class AgreementData(
    @SerializedName("Agreementhtml") var Agreementhtml: String? = null
)