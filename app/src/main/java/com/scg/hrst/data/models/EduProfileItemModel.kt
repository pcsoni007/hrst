package com.iqonic.quiz.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class EduProfileItemModel : Serializable {
    @SerializedName("Name")
    var name = ""
    @SerializedName("Info")
    var info = ""
    @SerializedName("SrNo")
    var srno = ""
    @SerializedName("Image")
    var image:Int? = 0
}