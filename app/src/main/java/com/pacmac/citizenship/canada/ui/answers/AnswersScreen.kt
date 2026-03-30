package com.pacmac.citizenship.canada.ui.answers

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pacmac.citizenship.canada.R
import com.pacmac.citizenship.canada.model.Answer
import com.pacmac.citizenship.canada.model.Question
import com.pacmac.citizenship.canada.ui.components.BannerAdView
import com.pacmac.citizenship.canada.ui.navigation.Screen
import com.pacmac.citizenship.canada.util.Constants

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AnswersScreen(
    navController: NavController,
    onShowInterstitial: () -> Unit,
    isAdFree: Boolean,
    vm: AnswersViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                AnswersEffect.ShowInterstitialAd -> onShowInterstitial()
                AnswersEffect.NavigateToResult -> navController.navigate(Screen.Result.route) {
                    popUpTo(Screen.Result.route) { inclusive = true }
                }
            }
        }
    }

    val isPassed = state.correctCount >= Constants.SUCCESS_ANSWER_COUNT
    val headerBg = if (isPassed) MaterialTheme.colorScheme.tertiaryContainer
    else MaterialTheme.colorScheme.errorContainer
    val onHeaderBg = if (isPassed) MaterialTheme.colorScheme.onTertiaryContainer
    else MaterialTheme.colorScheme.onErrorContainer

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Answers") },
                navigationIcon = {
                    IconButton(onClick = vm::onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BannerAdView(
                adUnitId = stringResource(R.string.banner_id_3),
                modifier = Modifier.fillMaxWidth(),
                isAdFree = isAdFree
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            stickyHeader {
                Surface(
                    color = headerBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${state.correctCount} / ${Constants.QUESTION_COUNT} correct",
                        style = MaterialTheme.typography.titleMedium,
                        color = onHeaderBg,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            itemsIndexed(state.questions) { index, question ->
                AnswerItem(index + 1, question)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun AnswerItem(number: Int, question: Question) {
    val isCorrect = question.isAnsweredCorrectly
    val bgColor by animateColorAsState(
        targetValue = if (isCorrect) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
        label = "itemBg"
    )

    Surface(color = bgColor) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Text(
                    text = "$number. ",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Correct: ${question.getAnswerString(question.correctAnswer)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            if (!isCorrect && question.userAnswer != Answer.UNKNOWN) {
                Text(
                    text = "Your answer: ${question.getAnswerString(question.userAnswer)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
