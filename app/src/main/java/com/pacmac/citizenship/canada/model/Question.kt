package com.pacmac.citizenship.canada.model

data class Question(
    val question: String,
    val a: String,
    val b: String,
    val c: String,
    val d: String,
    val correctAnswer: Answer,
    val userAnswer: Answer = Answer.UNKNOWN
) {
    val isAnsweredCorrectly: Boolean get() = userAnswer == correctAnswer

    fun getAnswerString(answer: Answer): String = when (answer) {
        Answer.ANSWER_A -> a
        Answer.ANSWER_B -> b
        Answer.ANSWER_C -> c
        Answer.ANSWER_D -> d
        Answer.UNKNOWN -> ""
    }
}
