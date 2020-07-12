package com.pacmac.citizenship.canada

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.citizenship.canada.model.QuestionObj
import com.pacmac.citizenship.canada.model.SuccessRate
import com.pacmac.citizenship.canada.util.Constants
import com.pacmac.citizenship.canada.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.net.URL
import kotlin.random.Random

class AppViewModel : ViewModel() {


    var correctAnswers = 0
    val successRate: MutableLiveData<SuccessRate> = MutableLiveData(SuccessRate(0,0))
    val allQuestionList: MutableLiveData<MutableList<QuestionObj>> by lazy {
        MutableLiveData<MutableList<QuestionObj>>()
    }
    val tempQuestionList: MutableLiveData<MutableList<QuestionObj>> by lazy {
        MutableLiveData<MutableList<QuestionObj>>()
    }
    val questionListShort: MutableLiveData<MutableList<QuestionObj>> by lazy {
        MutableLiveData<MutableList<QuestionObj>>()
    }

    fun getShortQuestionList(context: Context): MutableLiveData<MutableList<QuestionObj>> {
        if (allQuestionList.value?.size ?: 0 >= 20) {


            if (tempQuestionList.value == null || tempQuestionList.value?.size ?: 0 < 20) {
                tempQuestionList.value = ArrayList()
                for (q in allQuestionList.value!!) {
                    tempQuestionList.value?.add(QuestionObj(q.question, q.a, q.b, q.c, q.d, q.answer))
                }
            }

            val list = ArrayList<QuestionObj>()

            for (i in 0..19) {
                val randomIndex = Random(SystemClock.elapsedRealtime()).nextInt(0, tempQuestionList.value!!.size)
                list.add(QuestionObj(
                        tempQuestionList.value!!.get(randomIndex).question,
                        tempQuestionList.value!!.get(randomIndex).a,
                        tempQuestionList.value!!.get(randomIndex).b,
                        tempQuestionList.value!!.get(randomIndex).c,
                        tempQuestionList.value!!.get(randomIndex).d,
                        tempQuestionList.value!!.get(randomIndex).answer))

                tempQuestionList.value?.removeAt(randomIndex)
            }

            questionListShort.value = list

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
            allQuestionList.value = Utils.createListOfQuestions(context)
            allQuestionList.value?.shuffle(Random(System.currentTimeMillis()))
            getLastSuccessRate(context)
        }
    }

    fun getLastSuccessRate(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val sRate = Utils.getLastSuccessRate(context)
            successRate.value?.sum = sRate.sum
            successRate.value?.completedCounter = sRate.completedCounter
        }
    }

    fun updateSuccessRate(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.setLastSuccessRate(context, successRate.value!!)
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