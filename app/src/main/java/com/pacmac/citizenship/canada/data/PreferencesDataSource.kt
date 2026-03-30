package com.pacmac.citizenship.canada.data

import android.content.Context
import android.content.SharedPreferences
import com.pacmac.citizenship.canada.model.SuccessRate
import com.pacmac.citizenship.canada.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences
        get() = context.getSharedPreferences(Constants.APP_PREFERENCE_FILE, Context.MODE_PRIVATE)

    fun getSuccessRate(): SuccessRate {
        val sum = prefs.getInt(Constants.SUCCESS_RATE_SUM_PREF, 0)
        val count = prefs.getInt(Constants.SUCCESS_RATE_COUNT_PREF, 0)
        return SuccessRate(sum, count)
    }

    fun saveSuccessRate(rate: SuccessRate) {
        prefs.edit()
            .putInt(Constants.SUCCESS_RATE_SUM_PREF, rate.sum)
            .putInt(Constants.SUCCESS_RATE_COUNT_PREF, rate.completedCounter)
            .apply()
    }

    var isInsightUnlocked: Boolean
        get() = prefs.getBoolean(Constants.INSIGHT_UNLOCKED_PREF, false)
        set(value) { prefs.edit().putBoolean(Constants.INSIGHT_UNLOCKED_PREF, value).apply() }
}
