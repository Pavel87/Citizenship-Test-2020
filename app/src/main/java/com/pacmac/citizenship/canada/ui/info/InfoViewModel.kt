package com.pacmac.citizenship.canada.ui.info

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.citizenship.canada.data.PreferencesDataSource
import com.pacmac.citizenship.canada.data.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val quizRepository: QuizRepository,
    private val preferencesDataSource: PreferencesDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(InfoState())
    val state: StateFlow<InfoState> = _state.asStateFlow()

    private val _effects = Channel<InfoEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        val rate = quizRepository.getSuccessRate()
        val appVersion = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        }.getOrDefault("")
        _state.update {
            it.copy(
                appVersion = appVersion,
                questionsVersion = quizRepository.getQuestionsVersion(),
                totalQuestions = quizRepository.allQuestions.value.size,
                successRatePercent = rate.averagePercent,
                isInsightUnlocked = preferencesDataSource.isInsightUnlocked
            )
        }
    }

    fun onWatchAdClicked() {
        viewModelScope.launch { _effects.send(InfoEffect.ShowRewardedAd) }
    }

    fun onInsightUnlocked() {
        preferencesDataSource.isInsightUnlocked = true
        _state.update { it.copy(isInsightUnlocked = true) }
        viewModelScope.launch { _effects.send(InfoEffect.NavigateToInsight) }
    }

    fun onOpenInsightClicked() {
        viewModelScope.launch { _effects.send(InfoEffect.NavigateToInsight) }
    }

    fun onRatingClicked() {
        viewModelScope.launch { _effects.send(InfoEffect.OpenPlayStore) }
    }
}
