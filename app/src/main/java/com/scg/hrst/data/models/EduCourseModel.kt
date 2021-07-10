package com.iqonic.quiz.models

import com.google.gson.annotations.SerializedName

class EduCourseModel {
    @SerializedName("Name")
    var name = ""
    @SerializedName("Image")
    var image:Int? = 0
    @SerializedName("isSelected")
    var isSelected :Int? = 0
}