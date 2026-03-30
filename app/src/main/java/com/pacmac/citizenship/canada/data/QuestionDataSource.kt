package com.pacmac.citizenship.canada.data

import android.content.Context
import android.content.SharedPreferences
import com.pacmac.citizenship.canada.model.Answer
import com.pacmac.citizenship.canada.model.Question
import com.pacmac.citizenship.canada.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun loadLocalQuestions(): Result<List<Question>> = withContext(Dispatchers.IO) {
        runCatching {
            val prefs: SharedPreferences = context.getSharedPreferences(
                Constants.APP_PREFERENCE_FILE, Context.MODE_PRIVATE
            )
            val savedVersion = prefs.getInt(
                Constants.LAST_QUESTIONS_VERSION_PREF, Constants.JSON_VERSION
            ).coerceAtLeast(Constants.JSON_VERSION)

            val jsonString = if (savedVersion > Constants.JSON_VERSION) {
                File("${context.filesDir}/${Constants.LATEST_QUESTIONS_FILE}").bufferedReader()
                    .use { it.readText() }
            } else {
                context.assets.open("q_a_2026.json").bufferedReader().use { it.readText() }
            }
            parseQuestions(jsonString)
        }
    }

    suspend fun downloadAndCacheIfNewer(): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val prefs: SharedPreferences = context.getSharedPreferences(
                Constants.APP_PREFERENCE_FILE, Context.MODE_PRIVATE
            )
            val lastVersion = prefs.getInt(
                Constants.LAST_QUESTIONS_VERSION_PREF, Constants.JSON_VERSION
            ).coerceAtLeast(Constants.JSON_VERSION)

            val newVersionStr = URL(Constants.JSON_VERSION_URL).readText()
            if (newVersionStr.isNotBlank() && lastVersion < newVersionStr.toInt()) {
                val newQuestions = URL(Constants.QUESTION_LIST_URL).readText()
                File("${context.filesDir}/${Constants.LATEST_QUESTIONS_FILE}")
                    .bufferedWriter().use { it.write(newQuestions) }
                prefs.edit()
                    .putInt(Constants.LAST_QUESTIONS_VERSION_PREF, newVersionStr.toInt())
                    .apply()
                true
            } else {
                false
            }
        }
    }

    fun getQuestionsVersion(): Int {
        val prefs = context.getSharedPreferences(Constants.APP_PREFERENCE_FILE, Context.MODE_PRIVATE)
        return prefs.getInt(Constants.LAST_QUESTIONS_VERSION_PREF, Constants.JSON_VERSION)
            .coerceAtLeast(Constants.JSON_VERSION)
    }

    private fun parseQuestions(jsonString: String): List<Question> {
        val jsonArray = JSONArray(jsonString)
        return (0 until jsonArray.length()).map { i ->
            val obj = jsonArray.get(i) as JSONObject
            Question(
                question = obj.getString("question"),
                a = obj.getString("a"),
                b = obj.getString("b"),
                c = obj.getString("c"),
                d = obj.getString("d"),
                correctAnswer = Answer.getCorrectAnswer(obj.getString("answer"))
            )
        }
    }
}
