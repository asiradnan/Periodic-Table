package com.asiradnan.periodictable

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.asiradnan.periodictable.data.elements
import com.asiradnan.periodictable.ui.screens.ElementDetailScreen
import com.asiradnan.periodictable.ui.screens.HomeScreen

@Composable
fun PeriodicTableApp(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    isEnglish: Boolean,
    onLanguageChanged: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "homeScreen",
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("homeScreen") {
            HomeScreen(
                isDarkTheme = isDarkTheme,
                onThemeChanged = onThemeChanged,
                isEnglish = isEnglish,
                onLanguageChanged = onLanguageChanged,
                onElementClick = { atomicNumber ->
                    navController.navigate("elementDetail/$atomicNumber")
                },
            )
        }
        composable("elementDetail/{atomicNumber}") { backStackEntry ->
            val atomicNumber =
                backStackEntry.arguments?.getString("atomicNumber")?.toIntOrNull()
            val element = elements.find { it.atomicNumber == atomicNumber }
            if (element != null) {
                ElementDetailScreen(
                    element = element,
                    isDarkTheme = isDarkTheme,
                    isEnglish = isEnglish
                )
            }
        }
    }
}