package com.pacmac.citizenship.canada.ui.result

data class ResultState(
    val correctCount: Int = 0,
    val isPassed: Boolean = false,
    val successRatePercent: Int = 0,
    val isLoading: Boolean = false,
    val hasAnimated: Boolean = false
)

sealed class ResultEffect {
    object NavigateToQuiz : ResultEffect()
    object NavigateToAnswers : ResultEffect()
    object NavigateToAnswersFree : ResultEffect()
    object ShowRewardedForAnswers : ResultEffect()
}
