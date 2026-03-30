package com.pacmac.citizenship.canada.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.citizenship.canada.data.QuizRepository
import com.pacmac.citizenship.canada.model.Answer
import com.pacmac.citizenship.canada.model.Question
import com.pacmac.citizenship.canada.model.QuizSession
import com.pacmac.citizenship.canada.model.SuccessRate
import com.pacmac.citizenship.canada.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    private val _effects = Channel<QuizEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var timerJob: Job? = null

    init {
        initQuiz()
    }

    fun initQuiz() {
        if (_state.value.questions.isNotEmpty()) return
        val questions = quizRepository.generateQuizSession()
        _state.value = QuizState(questions = questions, timerSeconds = Constants.TEST_TIME)
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = _state.value.timerSeconds
            while (remaining >= 0) {
                _state.update {
                    it.copy(
                        timerSeconds = remaining,
                        isTimerWarning = remaining < Constants.WARN_TIME
                    )
                }
                if (remaining == 0) {
                    handleTimeExpired()
                    break
                }
                remaining--
                delay(1000L)
            }
        }
    }

    fun submitAnswer(answer: Answer) {
        if (answer == Answer.UNKNOWN) return
        val state = _state.value
        val updatedQuestion = state.questions[state.currentIndex].copy(userAnswer = answer)
        val updatedQuestions = state.questions.toMutableList().apply {
            this[state.currentIndex] = updatedQuestion
        }
        val newCorrectCount =
            if (updatedQuestion.isAnsweredCorrectly) state.correctCount + 1 else state.correctCount

        if (state.currentIndex < Constants.QUESTION_COUNT - 1) {
            _state.update {
                it.copy(
                    questions = updatedQuestions,
                    currentIndex = it.currentIndex + 1,
                    correctCount = newCorrectCount,
                    selectedAnswer = Answer.UNKNOWN
                )
            }
        } else {
            timerJob?.cancel()
            _state.update { it.copy(questions = updatedQuestions, correctCount = newCorrectCount) }
            completeQuiz(updatedQuestions, newCorrectCount)
        }
    }

    fun selectAnswer(answer: Answer) {
        _state.update { it.copy(selectedAnswer = answer) }
    }

    private fun handleTimeExpired() {
        val state = _state.value
        completeQuiz(state.questions, state.correctCount)
    }

    private fun completeQuiz(questions: List<Question>, correctCount: Int) {
        viewModelScope.launch {
            quizRepository.completedSession = QuizSession(questions, correctCount)
            val oldRate = quizRepository.getSuccessRate()
            val newRate = SuccessRate(
                sum = oldRate.sum + 100 * correctCount / Constants.QUESTION_COUNT,
                completedCounter = oldRate.completedCounter + 1
            )
            quizRepository.saveSuccessRate(newRate)
            _effects.send(QuizEffect.NavigateToResult)
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
