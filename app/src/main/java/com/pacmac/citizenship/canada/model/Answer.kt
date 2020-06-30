package com.pacmac.citizenship.canada.model

enum class Answer(val answer: String) {

    ANSWER_A("a"),
    ANSWER_B("b"),
    ANSWER_C("c"),
    ANSWER_D("d"),
    UNKNOWN("unknown");


    companion object {
        fun getCorrectAnswer(name: String): Answer {

            for (value in Answer.values()) {

                if (value.answer.equals(name)) {
                    return value;
                }
            }
            return UNKNOWN;
        }
    }
}