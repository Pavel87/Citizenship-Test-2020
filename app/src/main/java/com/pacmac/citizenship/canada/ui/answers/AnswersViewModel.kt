package com.pacmac.citizenship.canada.ui.answers

import androidx.lifecycle.SavedStateHandle
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
class AnswersViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val freeAccess: Boolean = savedStateHandle.get<Boolean>("freeAccess") ?: false

    private val _state = MutableStateFlow(AnswersState())
    val state: StateFlow<AnswersState> = _state.asStateFlow()

    private val _effects = Channel<AnswersEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        val session = quizRepository.completedSession
        _state.update {
            it.copy(
                questions = session?.questions ?: emptyList(),
                correctCount = session?.correctCount ?: 0
            )
        }
    }

    fun onBack() {
        viewModelScope.launch {
            if (!freeAccess) _effects.send(AnswersEffect.ShowInterstitialAd)
            _effects.send(AnswersEffect.NavigateToResult)
        }
    }
}
