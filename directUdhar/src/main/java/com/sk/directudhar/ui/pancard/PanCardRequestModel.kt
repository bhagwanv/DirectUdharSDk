package com.sk.directudhar.ui.pancard

data class PanCardRequestModel(
    val NameAsPAN: String,
    val EmailID: String,
    val PanNumber: String,
    val RefrralCode: String,
    val imageUrl: String,
    val checked: Boolean
)

