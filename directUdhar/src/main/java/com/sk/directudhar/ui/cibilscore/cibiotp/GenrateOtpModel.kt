package com.sk.directudhar.ui.cibilscore.cibiotp

data class GenrateOtpModel(val mobileNo: String, val stgOneHitId: String, val stgTwoHitId: String)
data class CiBilResponceModel(val stgOneHitId: String, val stgTwoHitId: String)
data class CiBilOTPResponceModel(val errorString:String,val stgOneHitId: String, val stgTwoHitId: String,val otpGenerationStatus:String)
data class PostOTPRequestModel(val mobileNo:String,val stgOneHitId: String, val stgTwoHitId: String,val otp:String,val LeadMasterId:Int)
