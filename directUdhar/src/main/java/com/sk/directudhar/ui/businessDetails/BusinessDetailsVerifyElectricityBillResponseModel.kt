package com.sk.directudhar.ui.businessDetails

data class BusinessDetailsVerifyElectricityBillResponseModel(val Data: Data,
                                                             val Msg: String,
                                                             val Result: Boolean)

data class Data(
    val bill_no: String,
    val bill_due_date: String,
    val consumer_number: String,
    val bill_amount: String,
    val bill_issue_date: String,
    val mobile_number: String,
    val amount_payable: String,
    val total_amount: String,
    val address: String,
    val consumer_name: String,
    val email_address: String,
    val bill_date: String,

    )

