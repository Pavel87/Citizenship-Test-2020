package com.pacmac.citizenship.canada.ui.info

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pacmac.citizenship.canada.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    navController: NavController,
    onShowRewarded: (onResult: (Boolean) -> Unit) -> Unit,
    onRemoveAds: () -> Unit,
    isAdFree: Boolean,
    vm: InfoViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                InfoEffect.NavigateToInsight -> navController.navigate(Screen.Insight.route)
                InfoEffect.ShowRewardedAd -> onShowRewarded { isUnlocked ->
                    if (isUnlocked) vm.onInsightUnlocked()
                }
                InfoEffect.OpenPlayStore -> {
                    val appPackage = context.packageName
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackage"))
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                headlineContent = { Text("App Version") },
                trailingContent = { Text(state.appVersion, style = MaterialTheme.typography.bodyLarge) }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Questions Version") },
                trailingContent = { Text("${state.questionsVersion}", style = MaterialTheme.typography.bodyLarge) }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Total Questions") },
                trailingContent = { Text("${state.totalQuestions}", style = MaterialTheme.typography.bodyLarge) }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Average Success Rate") },
                trailingContent = {
                    Text(
                        if (state.successRatePercent > 0) "${state.successRatePercent}%" else "N/A",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
            HorizontalDivider()

            // Insight row
            ListItem(
                headlineContent = {
                    Text(if (state.isInsightUnlocked) "Citizenship Test Insights" else "Unlock Test Insights")
                },
                supportingContent = {
                    if (!state.isInsightUnlocked) {
                        Text("Watch a short ad to unlock tips", style = MaterialTheme.typography.bodySmall)
                    }
                },
                leadingContent = {
                    Icon(
                        imageVector = if (state.isInsightUnlocked) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingContent = {
                    AnimatedContent(targetState = state.isInsightUnlocked, label = "insightBtn") { unlocked ->
                        TextButton(
                            onClick = if (unlocked) vm::onOpenInsightClicked else vm::onWatchAdClicked
                        ) {
                            Icon(
                                imageVector = if (unlocked) Icons.Outlined.OpenInBrowser else Icons.Outlined.PlayCircle,
                                contentDescription = null
                            )
                            Text(if (unlocked) "Open" else "Watch Ad")
                        }
                    }
                }
            )
            HorizontalDivider()

            // Remove Ads row
            ListItem(
                headlineContent = { Text(if (isAdFree) "Ads Removed" else "Remove Ads") },
                supportingContent = {
                    if (!isAdFree) Text("One-time purchase", style = MaterialTheme.typography.bodySmall)
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.MoneyOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingContent = {
                    if (!isAdFree) {
                        TextButton(onClick = onRemoveAds) {
                            Text("Buy")
                        }
                    }
                }
            )
            HorizontalDivider()

            // Rating row
            ListItem(
                headlineContent = { Text("Rate the App") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingContent = {
                    TextButton(onClick = vm::onRatingClicked) {
                        Text("Rate on Play Store")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
