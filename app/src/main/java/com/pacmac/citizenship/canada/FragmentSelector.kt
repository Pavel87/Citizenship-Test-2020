package com.pacmac.citizenship.canada

interface FragmentSelector {
    fun onStartTest()
    fun onTestComplete(updateSuccessRate: Boolean)
    fun onInsightFragmentRequested()
    fun onRewardedAdRequested(callback: (isUnlocked: Boolean)->Unit)
    fun onAnswersRequested()
    fun onInfoRequested()
    fun onLoadFullScreenAd()
}