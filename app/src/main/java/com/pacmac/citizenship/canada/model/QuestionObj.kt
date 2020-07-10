package com.pacmac.citizenship.canada.model

class QuestionObj(
        val question: String, val a: String, val b: String,
        val c: String, val d: String, val answer: Answer) {

    var userAnswer: Answer = Answer.UNKNOWN

    fun isCorrectAnswer(): Boolean {
        return userAnswer == answer
    }

    fun getAnswerString(answer: Answer): String {
        when (answer) {
            Answer.ANSWER_A -> return a
            Answer.ANSWER_B -> return b
            Answer.ANSWER_C -> return c
            Answer.ANSWER_D -> return d
            Answer.UNKNOWN -> return ""
        }
    }
}