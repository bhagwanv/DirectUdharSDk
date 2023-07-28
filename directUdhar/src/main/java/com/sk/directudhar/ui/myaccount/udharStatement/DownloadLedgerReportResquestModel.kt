package com.sk.directudhar.ui.myaccount.udharStatement

import com.google.gson.annotations.SerializedName

data class DownloadLedgerReportResquestModel(
    @SerializedName("AccountId") var AccountId: String? = null,
    @SerializedName("MonthType") var MonthType: Int? = null,
    @SerializedName("FromDate") var FromDate: String? = null,
    @SerializedName("ToDate") var ToDate: String? = null
)
