package com.pacmac.citizenship.canada

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.pacmac.citizenship.canada.util.Utils

class AdsHelper(val context: Context) {

    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null

    private var adShown = false


    private var rewardedAdId: String = context.resources.getString(R.string.rewarded_id_1)
    private var interstitialAdId: String = context.resources.getString(R.string.interstitial_id_1)
    private var adRequest = AdRequest.Builder().build()


    fun initializeFullScreenAd() {
        if (mInterstitialAd == null || adShown) {
            InterstitialAd.load(context, interstitialAdId, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                        mInterstitialAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                    println("PACMAC - onAdFailedToShowFullScreenContent: ${p0.cause}")
                                    mInterstitialAd = null
                                }

                                override fun onAdShowedFullScreenContent() {
                                    adShown = true
                                    println("PACMAC - onAdShowedFullScreenContent")
                                }

                                override fun onAdDismissedFullScreenContent() {
                                    println("PACMAC - onAdDismissedFullScreenContent")
                                    mInterstitialAd = null
                                }

                                override fun onAdImpression() {
                                    println("PACMAC - onAdImpression")
                                    mInterstitialAd = null
                                }
                            }
                        Log.d("PACMAC", "iAd loaded")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.d("PACMAC", "LoadError - ${loadAdError}")
                        mInterstitialAd = null
                    }
                })
        }
    }

    fun showInterestialAd(activity: Activity) {
        mInterstitialAd?.show(activity)
    }


    fun initializeRewardedAd() {
        RewardedAd.load(
            context,
            rewardedAdId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("PACMAC", "AD ERROR: ${adError?.message}")
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d("PACMAC", "Rewarded Ad was loaded.")
                    mRewardedAd = rewardedAd
                }
            })
    }


    fun showRewardedApp(activity: Activity, callback: (isUnlocked: Boolean) -> Unit) {
        mRewardedAd?.show(activity) {
            if (it.amount != 0) {
                Utils.isInsightUnlocked = true
                callback.invoke(true)
            } else {
                callback.invoke(false)
            }
        }
    }

//    fun attachRewardedCallback() {
//        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
//            override fun onAdShowedFullScreenContent() {
//                // Called when ad is shown.
//                Log.d("PACMAC", "Rewarded Ad was shown.")
//            }
//
//            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                // Called when ad fails to show.
//                Log.d("PACMAC", "Rewarded Ad failed to show.")
//            }
//
//            override fun onAdDismissedFullScreenContent() {
//                // Called when ad is dismissed.
//                // Set the ad reference to null so you don't show the ad a second time.
//                Log.d("PACMAC", "Rewarded Ad was dismissed.")
//                mRewardedAd = null
//                // show ad
//                view?.findViewById<TextView>(R.id.insightsLabel)?.text =
//                    "Citizenship Test Insights"
//                view?.findViewById<Button>(R.id.watchAdVideo)?.text = "Open"
//                isInsightUnlocked = true
//            }
//        }
//    }


}