package com.pacmac.citizenship.canada.ui.result

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pacmac.citizenship.canada.R
import com.pacmac.citizenship.canada.ui.components.BannerAdView
import com.pacmac.citizenship.canada.ui.navigation.Screen
import com.pacmac.citizenship.canada.util.Constants
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(
    navController: NavController,
    onShowRewarded: (onResult: (Boolean) -> Unit) -> Unit,
    isAdFree: Boolean,
    vm: ResultViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                ResultEffect.NavigateToQuiz -> navController.navigate(Screen.Quiz.route) {
                    popUpTo(Screen.Intro.route) { inclusive = false }
                }
                ResultEffect.NavigateToAnswers -> navController.navigate(Screen.Answers.route)
                ResultEffect.NavigateToAnswersFree -> navController.navigate(Screen.Answers.freeRoute())
                ResultEffect.ShowRewardedForAnswers -> onShowRewarded { earned ->
                    if (earned) vm.onFreeAnswersUnlocked()
                }
            }
        }
    }

    ResultContent(
        state = state,
        isAdFree = isAdFree,
        onTryAgain = vm::onTryAgain,
        onViewAnswers = vm::onViewAnswers,
        onWatchAdForAnswers = vm::onWatchAdForAnswers,
        onAnimationComplete = vm::markAnimated
    )
}

@Composable
fun ResultContent(
    state: ResultState,
    isAdFree: Boolean,
    onTryAgain: () -> Unit,
    onViewAnswers: () -> Unit,
    onWatchAdForAnswers: () -> Unit,
    onAnimationComplete: () -> Unit
) {
    // On return from Answers (hasAnimated = true) all start at their final values — no animation
    var colorRevealed by remember { mutableStateOf(state.hasAnimated) }
    var scoreTarget by remember { mutableIntStateOf(if (state.hasAnimated) state.correctCount else 0) }
    var successRateTarget by remember { mutableIntStateOf(if (state.hasAnimated) state.successRatePercent else 0) }

    LaunchedEffect(Unit) {
        if (state.hasAnimated) return@LaunchedEffect
        delay(300)
        scoreTarget = state.correctCount            // score count-up starts
        delay(1400)                                 // score tween is 1200ms + small pause
        colorRevealed = true
        successRateTarget = state.successRatePercent // success rate counts up at reveal
        onAnimationComplete()
    }

    val animatedScore by animateIntAsState(
        targetValue = scoreTarget,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "score"
    )

    val animatedSuccessRate by animateIntAsState(
        targetValue = successRateTarget,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "successRate"
    )

    val cardColor by animateColorAsState(
        targetValue = if (colorRevealed) {
            if (state.isPassed) MaterialTheme.colorScheme.tertiaryContainer
            else MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(500),
        label = "cardColor"
    )

    val onCardColor by animateColorAsState(
        targetValue = if (colorRevealed) {
            if (state.isPassed) MaterialTheme.colorScheme.onTertiaryContainer
            else MaterialTheme.colorScheme.onErrorContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(500),
        label = "onCardColor"
    )

    Scaffold(
        bottomBar = {
            BannerAdView(
                adUnitId = stringResource(R.string.banner_id_2),
                modifier = Modifier.fillMaxWidth(),
                isAdFree = isAdFree
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card — fixed height, color and icon animate at reveal
            Surface(
                color = cardColor,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Placeholder icon swaps to pass/fail icon at reveal
                    AnimatedContent(
                        targetState = colorRevealed,
                        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                        label = "resultIcon"
                    ) { revealed ->
                        Icon(
                            imageVector = when {
                                !revealed -> Icons.Outlined.Info
                                state.isPassed -> Icons.Filled.CheckCircle
                                else -> Icons.Filled.Cancel
                            },
                            contentDescription = null,
                            tint = onCardColor,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    AnimatedContent(
                        targetState = colorRevealed,
                        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                        label = "resultTitle"
                    ) { revealed ->
                        Text(
                            text = if (revealed) {
                                if (state.isPassed) "Congratulations!" else "Not Passed"
                            } else {
                                "Calculating..."
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            color = onCardColor
                        )
                    }
                    Text(
                        text = "$animatedScore / ${Constants.QUESTION_COUNT}",
                        style = MaterialTheme.typography.displayMedium,
                        color = onCardColor
                    )
                    AnimatedContent(
                        targetState = colorRevealed,
                        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                        label = "resultSubtitle"
                    ) { revealed ->
                        Text(
                            text = if (revealed) {
                                if (state.isPassed) "You passed the test!"
                                else "You need ${Constants.SUCCESS_ANSWER_COUNT} correct answers to pass."
                            } else {
                                "Please wait..."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = onCardColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            if (state.successRatePercent > 0) {
                Text(
                    text = "Your average success rate: $animatedSuccessRate%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                OutlinedButton(
                    onClick = onTryAgain,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Try Again")
                }
                Button(
                    onClick = onViewAnswers,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text("View Answers")
                }
            }

            if (!state.isPassed && !isAdFree) {
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = onWatchAdForAnswers,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text("Watch ad — unlock answers & skip exit ad")
                }
            }
        }
    }
}
