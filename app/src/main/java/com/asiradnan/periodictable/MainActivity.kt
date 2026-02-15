package com.asiradnan.periodictable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.asiradnan.periodictable.components.ChemicalElementsScreen
import com.asiradnan.periodictable.components.ElementDetailScreen
import com.asiradnan.periodictable.data.elements
import com.asiradnan.periodictable.ui.theme.PeriodicTableTheme
import com.asiradnan.periodictable.utils.ThemePreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember {
                mutableStateOf(ThemePreference.getDarkMode(this))
            }
            var isEnglish by remember {
                mutableStateOf(ThemePreference.getLanguage(this))
            }

            PeriodicTableTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    containerColor = if (isDarkTheme) Color.Black else Color.White
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "chemicalElements",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("chemicalElements") {
                            ChemicalElementsScreen(
                                navController = navController,
                                isDarkTheme = isDarkTheme,
                                onThemeChanged = { newTheme ->
                                    isDarkTheme = newTheme
                                    ThemePreference.saveDarkMode(this@MainActivity, newTheme)
                                },
                                isEnglish = isEnglish,
                                onLanguageChanged = { newLanguage ->
                                    isEnglish = newLanguage
                                    ThemePreference.saveLanguage(this@MainActivity, newLanguage)
                                }
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
            }
        }
    }
}