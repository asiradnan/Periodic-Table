package com.asiradnan.periodictable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.asiradnan.periodictable.ui.theme.PeriodicTableTheme


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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    PeriodicTableApp(
                        modifier = Modifier.padding(innerPadding),
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
            }
        }
    }
}


