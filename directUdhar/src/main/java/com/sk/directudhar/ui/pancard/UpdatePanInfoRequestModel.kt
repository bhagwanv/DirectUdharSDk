package com.sk.directudhar.ui.pancard

data class UpdatePanInfoRequestModel(
    val LeadMasterId: Int,
    val PanNo: String,
    val ImageUrl: String,
    val Name: String,
    val IsAcceptPP: Boolean,
)