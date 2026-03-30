package com.pacmac.citizenship.canada.ui.answers

import com.pacmac.citizenship.canada.model.Question

data class AnswersState(
    val questions: List<Question> = emptyList(),
    val correctCount: Int = 0
)

sealed class AnswersEffect {
    object NavigateToResult : AnswersEffect()
    object ShowInterstitialAd : AnswersEffect()
}
