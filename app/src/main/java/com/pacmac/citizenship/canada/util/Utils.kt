package com.pacmac.citizenship.canada.util

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.LinearLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.pacmac.citizenship.canada.model.Answer
import com.pacmac.citizenship.canada.model.QuestionObj
import com.pacmac.citizenship.canada.model.SuccessRate
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


object Utils {

    var isInsightUnlocked = false

    private fun loadJSONArrayString(context: Context, updatedVersion: Boolean): String {
        return if (updatedVersion) {
            File("${context.filesDir}/${Constants.LATEST_QUESTIONS_FILE}").bufferedReader()
                .use { it.readText() }
        } else {
            context.assets.open("q_a_2020.json").bufferedReader().use { it.readText() }
        }
    }

    var latestJSONVersion = Constants.JSON_VERSION

    fun createListOfQuestions(context: Context): MutableList<QuestionObj> {

        val preferences: SharedPreferences =
            context.getSharedPreferences(Constants.APP_PREFERENCE_FILE, Context.MODE_PRIVATE)
        latestJSONVersion =
            preferences.getInt(Constants.LAST_QUESTIONS_VERSION_PREF, Constants.JSON_VERSION)
                .coerceAtLeast(Constants.JSON_VERSION)

        val jsonArray =
            JSONArray(loadJSONArrayString(context, latestJSONVersion > Constants.JSON_VERSION))
        val list: MutableList<QuestionObj> = ArrayList()


        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.get(i) as JSONObject

            val questionObject = QuestionObj(
                jsonObject.getString("question"),
                jsonObject.getString("a"),
                jsonObject.getString("b"),
                jsonObject.getString("c"),
                jsonObject.getString("d"),
                Answer.getCorrectAnswer(jsonObject.getString("answer"))
            )
            list.add(questionObject)
        }
        return list
    }


    fun formatTimeLimit(seconds: Int): String {
        return "${String.format("%02d", seconds / 60)}:${String.format("%02d", seconds % 60)}"
    }

    fun showBannerAdView(
        view: View,
        context: Context,
        bannerID: Int
    ) {
        val isFreeOfAds = false

        if (isFreeOfAds) {
            view.setVisibility(View.GONE)
            return
        }
        val mAdView = AdView(context)
        mAdView.adSize = AdSize.BANNER
        mAdView.adUnitId = context.resources.getString(bannerID)
        (view as LinearLayout).addView(mAdView)
        val adRequest: AdRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }


    fun getLastSuccessRate(context: Context): SuccessRate {
        val preferences: SharedPreferences =
            context.getSharedPreferences(Constants.APP_PREFERENCE_FILE, Context.MODE_PRIVATE)
        val sum = preferences.getInt(Constants.SUCCESS_RATE_SUM_PREF, 0)
        val count = preferences.getInt(Constants.SUCCESS_RATE_COUNT_PREF, 0)

        return SuccessRate(sum, count)
    }

    fun setLastSuccessRate(context: Context, successRate: SuccessRate) {
        val preferences: SharedPreferences =
            context.getSharedPreferences(Constants.APP_PREFERENCE_FILE, Context.MODE_PRIVATE)

        preferences.edit()
            .putInt(Constants.SUCCESS_RATE_SUM_PREF, successRate.sum)
            .putInt(Constants.SUCCESS_RATE_COUNT_PREF, successRate.completedCounter)
            .apply()

    }
}