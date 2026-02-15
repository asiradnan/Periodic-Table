package com.asiradnan.periodictable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asiradnan.periodictable.data.Element
import com.asiradnan.periodictable.data.banglaNames
import com.asiradnan.periodictable.utils.*

@Composable
fun ElementDetailScreen(
    element: Element,
    isDarkTheme: Boolean,
    isEnglish: Boolean
) {
    val textColor = if (isDarkTheme) Color.White else Color.Black

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = if (isDarkTheme) Color.Black else Color.White)
    ) {
        val screenWidth = maxWidth

        // Adjust sizes based on screen size
        val boxSize = if (screenWidth < 600.dp) 96.dp else 128.dp
        val topSpacing = if (screenWidth < 600.dp) 24.dp else 48.dp
        val fontSizeLarge = if (screenWidth < 600.dp) 35.sp else 45.sp
        val fontSizeMedium = if (screenWidth < 600.dp) 25.sp else 35.sp
        val fontSizeSmall = if (screenWidth < 600.dp) 14.sp else 18.sp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(topSpacing))

            // Element Symbol Box
            Box(
                modifier = Modifier
                    .size(boxSize)
                    .shadow(8.dp, shape = RoundedCornerShape(24.dp))
                    .background(
                        color = if (isDarkTheme) Color.DarkGray.copy(alpha = 0.4f) else Color.White,
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEnglish) element.atomicNumber.toString() else NumberTranslator.translateToBangla(element.atomicNumber.toString()),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
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
                text = if (isEnglish) element.name else banglaNames.getOrElse(element.atomicNumber - 1) { element.name },
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
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                DetailRow(isEnglish, isDarkTheme, "Kind", if (isEnglish) element.kind else toBanglaKind(element.kind), fontSizeSmall)
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(isEnglish, isDarkTheme, "Atomic Mass", element.atomicMass.toString(), fontSizeSmall)
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(isEnglish, isDarkTheme, "Group", element.group?.toString() ?: "N/A", fontSizeSmall)
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(isEnglish, isDarkTheme, "Period", element.period?.toString() ?: "N/A", fontSizeSmall)
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(isEnglish, isDarkTheme, "Protons", element.atomicNumber.toString(), fontSizeSmall)
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(isEnglish, isDarkTheme, "Neutrons", (element.atomicMass.toInt().minus(element.atomicNumber)).toString(), fontSizeSmall)
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(isEnglish, isDarkTheme, "Electrons", element.atomicNumber.toString(), fontSizeSmall)
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(isEnglish, isDarkTheme, "State", if (isEnglish) element.state.displayName else toBanglaState(element.state), fontSizeSmall)
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(isEnglish, isDarkTheme, "Electronegativity", element.electronegativity?.toString() ?: "N/A", fontSizeSmall)
            }
        }
    }
}

@Composable
fun DetailRow(
    isEnglish: Boolean = true,
    isDarkTheme: Boolean,
    label: String,
    value: String,
    fontSize: TextUnit
) {
    val textColor = if (isDarkTheme) Color.LightGray else Color.Gray
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
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