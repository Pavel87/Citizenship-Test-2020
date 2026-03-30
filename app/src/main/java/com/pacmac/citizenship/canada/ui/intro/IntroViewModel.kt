package com.pacmac.citizenship.canada.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.citizenship.canada.data.QuizRepository
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
class IntroViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow(IntroState())
    val state: StateFlow<IntroState> = _state.asStateFlow()

    private val _effects = Channel<IntroEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (quizRepository.allQuestions.value.isEmpty()) {
                quizRepository.loadQuestions()
            }
        }
        viewModelScope.launch {
            quizRepository.refreshQuestions()
        }
    }

    fun onStartTestClicked() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            if (quizRepository.allQuestions.value.isEmpty()) {
                quizRepository.loadQuestions()
            }
            _state.update { it.copy(isLoading = false, questionsReady = true) }
            _effects.send(IntroEffect.NavigateToQuiz)
        }
    }

    fun onInfoClicked() {
        viewModelScope.launch { _effects.send(IntroEffect.NavigateToInfo) }
    }

    fun onStudyGuideClicked() {
        viewModelScope.launch { _effects.send(IntroEffect.OpenStudyGuide) }
    }

    fun onQuestionsReadyConsumed() {
        _state.update { it.copy(questionsReady = false) }
    }
}
