package com.sk.directudhar.ui.myaccount

import com.google.gson.annotations.SerializedName

data class UdharStatementModel(
    @SerializedName("DueDate") var DueDate: String? = null,
    @SerializedName("TransactionDate") var TransactionDate: String? = null,
    @SerializedName("TxnAmount") var TxnAmount: Double? = null,
    @SerializedName("DueAmount") var DueAmount: Double? = null,
    @SerializedName("TrasanctionId") var TrasanctionId: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("PaidAmount") var PaidAmount: Int? = null,
    @SerializedName("PaidDate") var PaidDate: String? = null,
    @SerializedName("IsUPIEnable") var IsUPIEnable: Boolean? = null
)
