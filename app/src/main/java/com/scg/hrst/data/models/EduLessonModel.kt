package com.iqonic.quiz.models

import com.google.gson.annotations.SerializedName

class EduLessonModel {
    @SerializedName("quizname")
    var heading = ""
    @SerializedName("Image")
    var Image:Int? = 0
    @SerializedName("Description")
    var description = ""
    @SerializedName("step")
    var step = ""
}