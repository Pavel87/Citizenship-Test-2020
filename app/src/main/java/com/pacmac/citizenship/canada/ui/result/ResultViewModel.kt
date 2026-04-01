package com.pacmac.citizenship.canada.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.citizenship.canada.data.QuizRepository
import com.pacmac.citizenship.canada.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state.asStateFlow()

    private val _effects = Channel<ResultEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        val session = quizRepository.completedSession
        val rate = quizRepository.getSuccessRate()
        _state.update {
            it.copy(
                correctCount = session?.correctCount ?: 0,
                isPassed = (session?.correctCount ?: 0) >= Constants.SUCCESS_ANSWER_COUNT,
                successRatePercent = rate.averagePercent
            )
        }
        // TODO: show in-app notification/banner when rate.averagePercent >= 95 to tell
        //  the user they are ready for the real citizenship test (requires enough sessions
        //  to be statistically meaningful — consider gating on rate.completedCounter >= 3)
    }

    fun onTryAgain() {
        viewModelScope.launch { _effects.send(ResultEffect.NavigateToQuiz) }
    }

    fun onViewAnswers() {
        viewModelScope.launch { _effects.send(ResultEffect.NavigateToAnswers) }
    }

    fun onWatchAdForAnswers() {
        viewModelScope.launch { _effects.send(ResultEffect.ShowRewardedForAnswers) }
    }

    fun onFreeAnswersUnlocked() {
        viewModelScope.launch { _effects.send(ResultEffect.NavigateToAnswersFree) }
    }

    fun markAnimated() {
        _state.update { it.copy(hasAnimated = true) }
    }
}
