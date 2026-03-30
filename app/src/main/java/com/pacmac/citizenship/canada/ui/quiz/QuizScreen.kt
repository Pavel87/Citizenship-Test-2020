package com.pacmac.citizenship.canada.ui.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pacmac.citizenship.canada.R
import com.pacmac.citizenship.canada.model.Answer
import com.pacmac.citizenship.canada.ui.components.BannerAdView
import com.pacmac.citizenship.canada.ui.navigation.Screen
import com.pacmac.citizenship.canada.util.Constants

@Composable
fun QuizScreen(
    navController: NavController,
    isAdFree: Boolean,
    vm: QuizViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                QuizEffect.NavigateToResult -> navController.navigate(Screen.Result.route) {
                    popUpTo(Screen.Intro.route) { inclusive = false }
                }
            }
        }
    }

    QuizContent(
        state = state,
        isAdFree = isAdFree,
        onAnswerSelected = vm::selectAnswer,
        onSubmit = { vm.submitAnswer(state.selectedAnswer) }
    )
}

@Composable
fun QuizContent(
    state: QuizState,
    isAdFree: Boolean,
    onAnswerSelected: (Answer) -> Unit,
    onSubmit: () -> Unit
) {
    val timerBg by animateColorAsState(
        targetValue = if (state.isTimerWarning) MaterialTheme.colorScheme.errorContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        label = "timerBg"
    )
    val timerTextColor by animateColorAsState(
        targetValue = if (state.isTimerWarning) MaterialTheme.colorScheme.onErrorContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "timerText"
    )

    val currentQuestion = state.questions.getOrNull(state.currentIndex)
    val progress = if (state.questions.isEmpty()) 0f
    else (state.currentIndex + 1).toFloat() / state.questions.size

    Scaffold(
        bottomBar = {
            BannerAdView(
                adUnitId = stringResource(R.string.banner_id_1),
                modifier = Modifier.fillMaxWidth(),
                isAdFree = isAdFree
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Timer + progress header
            Surface(color = timerBg) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Question ${state.currentIndex + 1} / ${Constants.QUESTION_COUNT}",
                        style = MaterialTheme.typography.titleSmall,
                        color = timerTextColor
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedContent(
                            targetState = state.timerSeconds / 60,
                            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                            label = "timerMinutes"
                        ) { minutes ->
                            Text(
                                text = "%02d".format(minutes),
                                style = MaterialTheme.typography.titleLarge,
                                color = timerTextColor
                            )
                        }
                        Text(
                            text = ":",
                            style = MaterialTheme.typography.titleLarge,
                            color = timerTextColor
                        )
                        AnimatedContent(
                            targetState = state.timerSeconds % 60,
                            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                            label = "timerSeconds"
                        ) { secs ->
                            Text(
                                text = "%02d".format(secs),
                                style = MaterialTheme.typography.titleLarge,
                                color = timerTextColor
                            )
                        }
                    }
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                drawStopIndicator = {}
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = currentQuestion?.question ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                HorizontalDivider()

                val answers = listOf(
                    Answer.ANSWER_A to (currentQuestion?.a ?: ""),
                    Answer.ANSWER_B to (currentQuestion?.b ?: ""),
                    Answer.ANSWER_C to (currentQuestion?.c ?: ""),
                    Answer.ANSWER_D to (currentQuestion?.d ?: "")
                )

                answers.forEach { (answer, text) ->
                    AnswerCard(
                        text = text,
                        isSelected = state.selectedAnswer == answer,
                        onClick = { onAnswerSelected(answer) }
                    )
                }

                Spacer(Modifier.height(8.dp))

                val isLastQuestion = state.currentIndex == Constants.QUESTION_COUNT - 1
                Button(
                    onClick = onSubmit,
                    enabled = state.selectedAnswer != Answer.UNKNOWN,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text(if (isLastQuestion) "Submit" else "Next")
                }
            }
        }
    }
}

@Composable
private fun AnswerCard(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surface,
        label = "cardBg"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        label = "cardScale"
    )
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = if (isSelected)
            BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        else null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
            else MaterialTheme.colorScheme.onSurface
        )
    }
}
