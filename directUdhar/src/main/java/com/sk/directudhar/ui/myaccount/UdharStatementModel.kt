package com.sk.directudhar.ui.myaccount

import com.google.gson.annotations.SerializedName

data class UdharStatementModel(
    @SerializedName("DueDate") var dueDate: String? = null,
    @SerializedName("TransactionDate") var transactionDate: String? = null,
    @SerializedName("TxnAmount") var txnAmount: Double? = null,
    @SerializedName("DueAmount") var dueAmount: Double? = null,
    @SerializedName("TrasanctionId") var transactionId: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("PaidAmount") var paidAmount: Double? = null,
    @SerializedName("PaidDate") var paidDate: String? = null,
    @SerializedName("IsUPIEnable") var isUPIEnable: Boolean? = null
)
