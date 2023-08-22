package com.sk.directudhar.ui.businessDetails

data class ManuallyUploadBillResponseModel(
     val Msg: String,
     val Result: Boolean,
     val Data: DataUrl
)

class DataUrl(val ImageUrl: String) {
}