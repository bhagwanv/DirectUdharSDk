package com.sk.directudhar.ui.mandate

data class BankListResponse(
    val error: Any,
    val liveBankList: List<LiveBank>
)