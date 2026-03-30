package com.pacmac.citizenship.canada.data

import com.pacmac.citizenship.canada.model.Question
import com.pacmac.citizenship.canada.model.QuizSession
import com.pacmac.citizenship.canada.model.SuccessRate
import com.pacmac.citizenship.canada.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val questionDataSource: QuestionDataSource,
    private val preferencesDataSource: PreferencesDataSource
) {
    private val _allQuestions = MutableStateFlow<List<Question>>(emptyList())
    val allQuestions: StateFlow<List<Question>> = _allQuestions.asStateFlow()

    // In-memory store shared between Quiz, Result, and Answers ViewModels
    var completedSession: QuizSession? = null

    // Pool for sampling without replacement across sessions
    private var questionPool: MutableList<Question> = mutableListOf()

    suspend fun loadQuestions() {
        questionDataSource.loadLocalQuestions().onSuccess { questions ->
            _allQuestions.value = questions.shuffled()
            if (questionPool.isEmpty()) {
                questionPool = questions.toMutableList()
            }
        }
    }

    suspend fun refreshQuestions(): Boolean {
        val updated = questionDataSource.downloadAndCacheIfNewer().getOrElse { false }
        if (updated) loadQuestions()
        return updated
    }

    fun generateQuizSession(): List<Question> {
        if (questionPool.size < Constants.QUESTION_COUNT) {
            questionPool = _allQuestions.value.toMutableList()
        }
        val selected = mutableListOf<Question>()
        repeat(minOf(Constants.QUESTION_COUNT, questionPool.size)) {
            val idx = (0 until questionPool.size).random()
            selected.add(questionPool.removeAt(idx))
        }
        return selected
    }

    fun getSuccessRate(): SuccessRate = preferencesDataSource.getSuccessRate()

    fun saveSuccessRate(rate: SuccessRate) = preferencesDataSource.saveSuccessRate(rate)

    fun getQuestionsVersion(): Int = questionDataSource.getQuestionsVersion()
}
