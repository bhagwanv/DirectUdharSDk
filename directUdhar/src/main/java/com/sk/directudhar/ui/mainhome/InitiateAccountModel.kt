package com.sk.directudhar.ui.mainhome

data class InitiateAccountModel(
     val Msg: String,
     val Result: Boolean,
     val Data: DynamicSequenceNo
)

class DynamicSequenceNo(val LeadMasterId: Int, val SequenceNo: Int) {
}
