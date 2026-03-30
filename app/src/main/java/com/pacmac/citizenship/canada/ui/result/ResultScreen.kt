package com.pacmac.citizenship.canada.ui.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
        onWatchAdForAnswers = vm::onWatchAdForAnswers
    )
}

@Composable
fun ResultContent(
    state: ResultState,
    isAdFree: Boolean,
    onTryAgain: () -> Unit,
    onViewAnswers: () -> Unit,
    onWatchAdForAnswers: () -> Unit
) {
    var iconVisible by remember { mutableStateOf(false) }
    var animTarget by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.correctCount) {
        iconVisible = true
        animTarget = state.correctCount
    }

    val animatedScore by animateIntAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = 800),
        label = "score"
    )

    val resultColor = if (state.isPassed)
        MaterialTheme.colorScheme.tertiaryContainer
    else
        MaterialTheme.colorScheme.errorContainer

    val onResultColor = if (state.isPassed)
        MaterialTheme.colorScheme.onTertiaryContainer
    else
        MaterialTheme.colorScheme.onErrorContainer

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
            Surface(
                color = resultColor,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnimatedVisibility(
                        visible = iconVisible,
                        enter = scaleIn()
                    ) {
                        Icon(
                            imageVector = if (state.isPassed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                            contentDescription = null,
                            tint = onResultColor,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    Text(
                        text = if (state.isPassed) "Congratulations!" else "Not Passed",
                        style = MaterialTheme.typography.headlineMedium,
                        color = onResultColor
                    )
                    Text(
                        text = "$animatedScore / ${Constants.QUESTION_COUNT}",
                        style = MaterialTheme.typography.displayMedium,
                        color = onResultColor
                    )
                    Text(
                        text = if (state.isPassed) "You passed the test!"
                        else "You need ${Constants.SUCCESS_ANSWER_COUNT} correct answers to pass.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = onResultColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            if (state.successRatePercent > 0) {
                Text(
                    text = "Your average success rate: ${state.successRatePercent}%",
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
                    modifier = Modifier.padding(horizontal = 24.dp)
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
