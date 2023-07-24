package com.sk.directudhar.ui.mandate

data class LiveBank(
    val activeFrm: String,
    val bankId: String,
    val bankName: String,
    val dcActiveFrom: String,
    val debitcardFlag: String,
    val netbankFlag: String
)