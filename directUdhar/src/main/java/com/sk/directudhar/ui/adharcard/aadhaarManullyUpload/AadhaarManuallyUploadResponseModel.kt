package com.sk.directudhar.ui.adharcard.aadhaarManullyUpload

data class AadhaarManuallyUploadResponseModel(
     val Msg: String,
     val Result: Boolean,
     val Data: Data
)

class Data(val LeadMasterId: Int, val SequenceNo: Int) {
}
