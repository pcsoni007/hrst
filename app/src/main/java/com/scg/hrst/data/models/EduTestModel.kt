package com.iqonic.quiz.models

import com.google.gson.annotations.SerializedName

class EduTestModel {
    @SerializedName("quizname")
    var heading = ""
    @SerializedName("Image")
    var Image:Int? = 0
    @SerializedName("Description")
    var description = ""
    @SerializedName("type")
    var type = ""
    @SerializedName("status")
    var status = ""
}