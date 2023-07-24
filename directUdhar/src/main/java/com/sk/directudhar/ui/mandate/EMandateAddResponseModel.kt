package com.sk.directudhar.ui.mandate

data class EMandateAddResponseModel(
    val error: String? = "",
    val request: Request,
    val status: Boolean,
    val url: String
)