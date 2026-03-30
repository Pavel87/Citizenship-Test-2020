package com.pacmac.citizenship.canada.ui.intro

data class IntroState(
    val isLoading: Boolean = false,
    val questionsReady: Boolean = false
)

sealed class IntroEffect {
    object NavigateToQuiz : IntroEffect()
    object NavigateToInfo : IntroEffect()
    object OpenStudyGuide : IntroEffect()
}
