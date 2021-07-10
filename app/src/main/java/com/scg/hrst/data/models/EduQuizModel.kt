package com.iqonic.quiz.models

import com.google.gson.annotations.SerializedName

class EduQuizModel {
    @SerializedName("quizName")
    var quizname = ""
    @SerializedName("totalQuiz")
    var totalQuiz = ""
    @SerializedName("Score")
    var Score = ""
    @SerializedName("QuizImage")
    var QuizImage:Int? = 0
}