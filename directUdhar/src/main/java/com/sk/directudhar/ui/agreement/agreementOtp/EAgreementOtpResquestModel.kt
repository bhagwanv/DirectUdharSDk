package com.sk.directudhar.ui.agreement.agreementOtp

data class EAgreementOtpResquestModel(
    val IsChecked: Boolean,
    val LeadMasterId: Int,
    val OtpNo: Int,
    val OtpTxnNo: String
)