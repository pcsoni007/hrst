package com.iqonic.quiz.models

import com.google.gson.annotations.SerializedName

class EduDiscussModel {
    @SerializedName("Name")
    var name = ""
    @SerializedName("ProfileImage")
    var profileImage:Int? = 0
    @SerializedName("Description")
    var description = ""
    @SerializedName("Duration")
    var duration = ""
}