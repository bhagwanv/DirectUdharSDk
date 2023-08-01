package com.sk.directudhar.ui.myaccount.udharStatement

import com.google.gson.annotations.SerializedName

data class HistoryResponseModel(
    @SerializedName("TrasanctionId") var TrasanctionId: String? = null,
    @SerializedName("CreatedDate") var CreatedDate: String? = null,
    @SerializedName("Amount") var Amount: Int? = null,
    @SerializedName("PaymentRefNo") var PaymentRefNo: String? = null,
    @SerializedName("Status") var Status: String? = null
)
