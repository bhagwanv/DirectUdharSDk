package com.sk.directudhar.ui.mandate

data class EMandateAddRequestModel(
    val AccountNo: String,
    val AccountType: String,
    val BankName: String,
    val Channel: String,
    val IfscCode: String,
    val IsNewRegistration: Boolean,
    val LeadMasterId: Int,
    val NachType: String,
    val Name: Any,
    val RelationshipTypes: Any,
)