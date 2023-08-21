package com.sk.directudhar.ui.personalDetails

data class GetPersonalInformationResponseModel(
    val Data: PersonalInformation)
data class PersonalInformation(val Address: String,
                val EmailId: String,
                val FirstName: String,
                val Gender: String,
                val LastName: String,
                val LeadMasterId: Int,
                val MobileNo: String,
                val PanNumber: String,
                val PinCode: String,
                val dateOfBirth: String,
                val City:String,
                val StateCode:String,
                val FlatNo:String)