package com.pacmac.citizenship.canada.ui.quiz

import com.pacmac.citizenship.canada.model.Answer
import com.pacmac.citizenship.canada.model.Question
import com.pacmac.citizenship.canada.util.Constants

data class QuizState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: Answer = Answer.UNKNOWN,
    val timerSeconds: Int = Constants.TEST_TIME,
    val isTimerWarning: Boolean = false,
    val correctCount: Int = 0
)

sealed class QuizEffect {
    object NavigateToResult : QuizEffect()
}
