package com.sk.directudhar.ui.pancard

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PanCardRequestModel: Serializable {
    @SerializedName("Base64Image")
    private var image: String? = null

    @SerializedName("LeadMasterId")
    private var leadMasterId: Int = 0

    constructor(image: String?, leadMasterId: Int) {
        this.image = image
        this.leadMasterId = leadMasterId
    }
}
