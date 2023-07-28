package com.sk.directudhar.ui.myaccount.udharStatement

import com.google.gson.annotations.SerializedName

data class TransactionDetailResponseModel(
    @SerializedName("TxnAmount") var TxnAmount: Double? = null,
    @SerializedName("TrasanctionId") var TrasanctionId: String? = null,
    @SerializedName("TrasanctionType") var TrasanctionType: String? = null,
    @SerializedName("TxnId") var TxnId: Long? = null
)
