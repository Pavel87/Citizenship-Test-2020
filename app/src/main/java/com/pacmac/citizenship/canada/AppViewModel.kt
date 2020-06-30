package com.pacmac.citizenship.canada

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.citizenship.canada.model.QuestionObj
import com.pacmac.citizenship.canada.util.Utils
import kotlinx.coroutines.launch
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
            questionListShort.value = allQuestionList.value!!.subList(0, 20)
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
}