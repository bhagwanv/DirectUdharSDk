package com.sk.directudhar.ui.myaccount.udharStatement

import com.google.gson.annotations.SerializedName

data class LedgerReportResponseModel(
    @SerializedName("file") var file: String? = null,
    @SerializedName("error") var error: String? = null
)
