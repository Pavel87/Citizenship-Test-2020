package com.pacmac.citizenship.canada

interface FragmentSelector {
    fun onStartTest()
    fun onTestComplete(updateSuccessRate: Boolean)
    fun onAnswersRequested()
    fun onInfoRequested()
    fun onLoadFullScreenAd()
}