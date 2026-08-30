package com.asiradnan.periodictable.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asiradnan.periodictable.R
import com.asiradnan.periodictable.data.Element
import com.asiradnan.periodictable.data.elements
import com.asiradnan.periodictable.utils.NumberTranslator
import com.asiradnan.periodictable.utils.banglaNames
import com.asiradnan.periodictable.utils.toBanglaKind

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    isEnglish: Boolean,
    onLanguageChanged: (Boolean) -> Unit,
    onElementClick: (Int) -> Unit,
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val filteredElements = remember(searchQuery.text) {
        if (isEnglish) {
            elements.filter {
                it.name.contains(searchQuery.text, ignoreCase = true) ||
                        it.symbol.contains(searchQuery.text, ignoreCase = true)
            }
        } else {
            elements.filterIndexed { index, element ->
                index < banglaNames.size && banglaNames[index].contains(
                    searchQuery.text,
                    ignoreCase = true
                )
                        || element.symbol.contains(searchQuery.text, ignoreCase = true)
            }
        }
    }
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 16.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onThemeChanged(!isDarkTheme) }
            ) {
                Icon(
                    imageVector = if (isDarkTheme) ImageVector.vectorResource(id = R.drawable.light_mode_24dp_5f6368_fill0_wght400_grad0_opsz24) else ImageVector.vectorResource(
                        id = R.drawable.dark_mode_24dp_5f6368_fill0_wght400_grad0_opsz24
                    ),
                    contentDescription = "Theme Toggle",
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isDarkTheme) "Light" else "Dark",
                    color = textColor
                )
            }

            // Language Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onLanguageChanged(!isEnglish) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.language_24dp_5f6368_fill0_wght400_grad0_opsz24),
                    contentDescription = "Language Toggle",
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isEnglish) "বাংলা" else "English",
                    color = textColor
                )
            }
        }
        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)  // Adjust height to make it smaller
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    color = if (isDarkTheme) Color.DarkGray else Color.White,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),  // Adjust padding for better alignment
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)  // Adjust icon size
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.text.isEmpty()) {
                                Text(
                                    text = if (isEnglish) "Search..." else "অনুসন্ধান করুন...",
                                    color = Color.Gray,
                                    fontSize = 14.sp  // Adjust font size
                                )
                            }
                            innerTextField()  // Render the actual text field
                        }
                        if (searchQuery.text.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = TextFieldValue("") },
                                modifier = Modifier.size(18.dp)  // Adjust icon size
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear Icon",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                },
                cursorBrush =
                    if (isDarkTheme) Brush.verticalGradient(listOf(Color.White, Color.White))
                    else Brush.verticalGradient(listOf(Color.Black, Color.Black)),
                textStyle = LocalTextStyle.current.copy(
                    color = textColor,
                    fontSize = 14.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Elements List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredElements, key = { it.atomicNumber }) { element ->
                ElementCard(
                    element = element,
                    isDarkTheme = isDarkTheme,
                    isEnglish = isEnglish,
//                    onClick = { selectedElement ->
//                        navController.navigate("elementDetail/${selectedElement.atomicNumber}")
//                    }
                    onClick = onElementClick
                )
            }
        }
    }
}

@Composable
fun ElementCard(
    element: Element,
    isDarkTheme: Boolean,
    isEnglish: Boolean,
    onClick: (Int) -> Unit
) {
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Card(
        onClick = { onClick(element.atomicNumber) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
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
                Text(
                    text = if (isEnglish) element.name else banglaNames[element.atomicNumber - 1],
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                Text(
                    text = if (isEnglish) element.kind else toBanglaKind(element.kind),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) Color.LightGray else Color.Gray
                )
            }
        }
    }
}




