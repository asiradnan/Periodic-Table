package com.asiradnan.periodictable.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asiradnan.periodictable.data.Element
import com.asiradnan.periodictable.utils.NumberTranslator
import com.asiradnan.periodictable.utils.banglaNames
import com.asiradnan.periodictable.utils.toBanglaKind
import com.asiradnan.periodictable.utils.toBanglaLabel
import com.asiradnan.periodictable.utils.toBanglaState
import androidx.compose.foundation.layout.Row

@Composable
fun ElementDetailScreen(
    element: Element,
    isDarkTheme: Boolean,
    isEnglish: Boolean
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val scrollState = rememberScrollState()

    // Adjust sizes based on screen width
    val boxSize = if (screenWidth < 600.dp) 96.dp else 128.dp
    val topSpacing = if (screenWidth < 600.dp) 24.dp else 48.dp
    val fontSizeLarge = if (screenWidth < 600.dp) 35.sp else 45.sp
    val fontSizeMedium = if (screenWidth < 600.dp) 25.sp else 35.sp
    val fontSizeSmall = if (screenWidth < 600.dp) 14.sp else 18.sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = if (isDarkTheme) Color.Black else Color.White)
            .verticalScroll(scrollState) // Enable scrolling
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(topSpacing))

        Box(
            modifier = Modifier
                .size(boxSize)
                .shadow(
                    8.dp,
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = if (isDarkTheme) Color.DarkGray.copy(alpha = 0.4f) else Color.White,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isEnglish)
                    element.atomicNumber.toString()
                else
                    NumberTranslator.translateToBangla(element.atomicNumber.toString()),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                color = textColor
            )

            Text(
                text = element.symbol,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                fontSize = fontSizeLarge,
                color = textColor
            )
        }

        Spacer(modifier = Modifier.height(topSpacing))

        // Element Name
        Text(
            text = if (isEnglish) element.name else banglaNames[element.atomicNumber - 1],
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSizeMedium,
            color = textColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Electron Configuration
        Text(
            text = element.electronConfiguration,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDarkTheme) Color.LightGray else Color.Gray,
            fontFamily = FontFamily.SansSerif,
            fontSize = fontSizeSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(if (screenWidth < 600.dp) 32.dp else 64.dp))

        // Details List
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            DetailRow(
                isEnglish = isEnglish,
                isDarkTheme = isDarkTheme,
                label = "Kind",
                value = if (isEnglish) element.kind else toBanglaKind(element.kind),
                fontSize = fontSizeSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow(
                isEnglish = isEnglish,
                isDarkTheme = isDarkTheme,
                label = "Atomic Mass",
                value = element.atomicMass.toString(),
                fontSize = fontSizeSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow(
                isEnglish = isEnglish,
                isDarkTheme = isDarkTheme,
                label = "Group",
                value = element.group?.toString() ?: "N/A",
                fontSize = fontSizeSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow(
                isEnglish = isEnglish,
                isDarkTheme = isDarkTheme,
                label = "Period",
                value = element.period?.toString() ?: "N/A",
                fontSize = fontSizeSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow(
                isEnglish = isEnglish,
                isDarkTheme = isDarkTheme,
                label = "Protons",
                value = element.atomicNumber.toString(),
                fontSize = fontSizeSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow(
                isEnglish = isEnglish,
                isDarkTheme = isDarkTheme,
                label = "Neutrons",
                value = (element.atomicMass?.toInt()?.minus(element.atomicNumber))?.toString()
                    ?: "N/A",
                fontSize = fontSizeSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow(
                isEnglish = isEnglish,
                isDarkTheme = isDarkTheme,
                label = "Electrons",
                value = element.atomicNumber.toString(),
                fontSize = fontSizeSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow(
                isEnglish = isEnglish,
                isDarkTheme = isDarkTheme,
                label = "State",
                value = if (isEnglish) element.state else toBanglaState(element.state),
                fontSize = fontSizeSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow(
                isEnglish = isEnglish,
                isDarkTheme = isDarkTheme,
                label = "Electronegativity",
                value = element.electronegativity?.toString() ?: "N/A",
                fontSize = fontSizeSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(24.dp)) // Extra space at bottom
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    isEnglish: Boolean = true,
    isDarkTheme: Boolean,
    fontSize: TextUnit
) {
    val textColor = if (isDarkTheme) Color.LightGray else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (isEnglish) label else toBanglaLabel(label),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontFamily = FontFamily.SansSerif,
            fontSize = fontSize,
            modifier = Modifier.padding(start = 6.dp)
        )
        Text(
            text = if (isEnglish) value else NumberTranslator.translateToBangla(value),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontFamily = FontFamily.SansSerif,
            fontSize = fontSize,
            modifier = Modifier.padding(end = 6.dp)
        )
    }
}
