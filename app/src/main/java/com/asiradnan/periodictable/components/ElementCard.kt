package com.asiradnan.periodictable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.asiradnan.periodictable.data.Element
import com.asiradnan.periodictable.data.banglaNames
import com.asiradnan.periodictable.utils.NumberTranslator
import com.asiradnan.periodictable.utils.toBanglaKind
import com.asiradnan.periodictable.utils.toBanglaState

@Composable
fun ElementCard(
    element: Element,
    isDarkTheme: Boolean,
    isEnglish: Boolean,
    onClick: (Element) -> Unit
) {
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick(element) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .padding(start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                    .background(
                        color = if (isDarkTheme) Color.DarkGray.copy(alpha = 0.4f) else Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEnglish)
                        element.atomicNumber.toString()
                    else
                        NumberTranslator.translateToBangla(element.atomicNumber.toString()),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp),
                    color = textColor
                )
                Text(
                    text = element.symbol,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            Column {
                // SAFER ACCESS HERE: Uses getOrElse to prevent crashing
                val displayName = if (isEnglish) {
                    element.name
                } else {
                    banglaNames.getOrElse(element.atomicNumber - 1) { element.name }
                }

                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                Text(
                    text = if (isEnglish) element.state.displayName else toBanglaState(element.state),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) Color.LightGray else Color.Gray
                )
            }
        }
    }
}