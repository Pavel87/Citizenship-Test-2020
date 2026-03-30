package com.pacmac.citizenship.canada.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.pacmac.citizenship.canada.R
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@ActivityRetainedScoped
class AdsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null

    private val adRequest = AdRequest.Builder().build()
    private val interstitialAdId: String = context.resources.getString(R.string.interstitial_id_1)
    private val rewardedAdId: String = context.resources.getString(R.string.rewarded_id_1)

    fun initInterstitial() {
        InterstitialAd.load(context, interstitialAdId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    mInterstitialAd = ad
                    mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() { mInterstitialAd = null }
                        override fun onAdFailedToShowFullScreenContent(e: AdError) { mInterstitialAd = null }
                        override fun onAdImpression() { mInterstitialAd = null }
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d("AdsRepository", "Interstitial load error: $error")
                    mInterstitialAd = null
                }
            })
    }

    fun showInterstitial(activity: Activity) {
        mInterstitialAd?.show(activity)
    }

    fun initRewarded() {
        RewardedAd.load(context, rewardedAdId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { mRewardedAd = ad }
            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.d("AdsRepository", "Rewarded load error: $error")
                mRewardedAd = null
            }
        })
    }

    /**
     * Shows the rewarded ad and suspends until the user either earns the reward (returns true)
     * or dismisses/fails (returns false). Handles the case where ad is dismissed without reward.
     */
    suspend fun showRewarded(activity: Activity): Boolean = suspendCancellableCoroutine { cont ->
        val ad = mRewardedAd
        if (ad == null) {
            cont.resume(false)
            return@suspendCancellableCoroutine
        }

        var rewarded = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mRewardedAd = null
                if (cont.isActive) cont.resume(rewarded)
            }

            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                mRewardedAd = null
                if (cont.isActive) cont.resume(false)
            }
        }

        ad.show(activity) { reward ->
            rewarded = reward.amount != 0
        }
    }
}
