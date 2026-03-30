package com.pacmac.citizenship.canada.ui.info

data class InfoState(
    val appVersion: String = "",
    val questionsVersion: Int = 0,
    val totalQuestions: Int = 0,
    val successRatePercent: Int = 0,
    val isInsightUnlocked: Boolean = false
)

sealed class InfoEffect {
    object NavigateToInsight : InfoEffect()
    object ShowRewardedAd : InfoEffect()
    object OpenPlayStore : InfoEffect()
}
