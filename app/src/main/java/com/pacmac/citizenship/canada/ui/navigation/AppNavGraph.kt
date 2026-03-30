package com.pacmac.citizenship.canada.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pacmac.citizenship.canada.ui.answers.AnswersScreen
import com.pacmac.citizenship.canada.ui.info.InfoScreen
import com.pacmac.citizenship.canada.ui.insight.InsightScreen
import com.pacmac.citizenship.canada.ui.intro.IntroScreen
import com.pacmac.citizenship.canada.ui.quiz.QuizScreen
import com.pacmac.citizenship.canada.ui.result.ResultScreen

sealed class Screen(val route: String) {
    object Intro   : Screen("intro")
    object Quiz    : Screen("quiz")
    object Result  : Screen("result")
    object Answers : Screen("answers?freeAccess={freeAccess}") {
        fun freeRoute() = "answers?freeAccess=true"
    }
    object Info    : Screen("info")
    object Insight : Screen("insight")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isAdFree: Boolean,
    onShowInterstitial: () -> Unit,
    onShowRewarded: (onResult: (Boolean) -> Unit) -> Unit,
    onRemoveAds: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Intro.route,
        enterTransition = { slideInHorizontally { it } },
        exitTransition = { slideOutHorizontally { -it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it } }
    ) {
        composable(Screen.Intro.route)   { IntroScreen(navController) }
        composable(Screen.Quiz.route)    { QuizScreen(navController, isAdFree) }
        composable(Screen.Result.route)  {
            ResultScreen(navController, onShowRewarded, isAdFree)
        }
        composable(
            route = Screen.Answers.route,
            arguments = listOf(
                navArgument("freeAccess") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            AnswersScreen(navController, onShowInterstitial, isAdFree)
        }
        composable(Screen.Info.route)    { InfoScreen(navController, onShowRewarded, onRemoveAds, isAdFree) }
        composable(Screen.Insight.route) { InsightScreen(navController) }
    }
}
