package com.pacmac.citizenship.canada

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.citizenship.canada.model.QuestionObj
import com.pacmac.citizenship.canada.util.Constants
import com.pacmac.citizenship.canada.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.net.URL
import kotlin.random.Random

class AppViewModel : ViewModel() {


    var correctAnswers = 0;
    val allQuestionList: MutableLiveData<MutableList<QuestionObj>> by lazy {
        MutableLiveData<MutableList<QuestionObj>>()
    }
    val questionListShort: MutableLiveData<MutableList<QuestionObj>> by lazy {
        MutableLiveData<MutableList<QuestionObj>>()
    }

    fun getShortQuestionList(context: Context): MutableLiveData<MutableList<QuestionObj>> {
        if (allQuestionList.value!!.size >= 20) {
            allQuestionList.value!!.shuffle(Random(System.currentTimeMillis()))
            val randomIndex = Random(SystemClock.elapsedRealtime())
                    .nextInt(0, allQuestionList.value!!.size - Constants.QUESTION_COUNT)
            questionListShort.value = allQuestionList.value!!.subList(randomIndex, randomIndex + Constants.QUESTION_COUNT)

        } else {
            loadQuestionList(context)
        }
        return questionListShort
    }

    fun getQuestionList(): MutableList<QuestionObj>? {
        return questionListShort.value
    }

    fun loadQuestionList(context: Context) {
        viewModelScope.launch {
            allQuestionList.value = Utils.createListOfQuestions(context);
        }
    }

    fun downloadLatestQuestions(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {

            try {


                val preferences: SharedPreferences =
                        context.getSharedPreferences(Constants.APP_PREFERENCE_FILE, Context.MODE_PRIVATE)
                val lastVersion: Int =
                        preferences.getInt(Constants.LAST_QUESTIONS_VERSION_PREF, Constants.JSON_VERSION)

                val newVersion = URL(Constants.JSON_VERSION_URL).readText()

                if (!newVersion.isNullOrBlank() && lastVersion != newVersion.toInt()) {
                    val newQuestions = URL(Constants.QUESTION_LIST_URL).readText()
                    File("${context.filesDir}/${Constants.LATEST_QUESTIONS_FILE}").bufferedWriter()
                            .use { out ->
                                out.write(newQuestions)
                                preferences.edit()
                                        .putInt(Constants.LAST_QUESTIONS_VERSION_PREF, newVersion.toInt())
                                        .commit()
                            }

                    loadQuestionList(context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}