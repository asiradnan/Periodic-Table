package com.asiradnan.periodictable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.asiradnan.periodictable.R
import com.asiradnan.periodictable.data.ElementState
import com.asiradnan.periodictable.data.banglaNames
import com.asiradnan.periodictable.data.elements
import com.asiradnan.periodictable.utils.filterElements

@Composable
fun ChemicalElementsScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    isEnglish: Boolean,
    onLanguageChanged: (Boolean) -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // FILTER STATE
    var selectedStateFilter by remember { mutableStateOf<ElementState?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }

    val filteredElements = remember(searchQuery.text, selectedStateFilter, isEnglish) {
        filterElements(
            allElements = elements,
            banglaNamesList = banglaNames,
            query = searchQuery.text,
            stateFilter = selectedStateFilter,
            isEnglish = isEnglish
        )
    }

    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(color = backgroundColor)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Bar (Theme & Language)
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Theme Toggle
            Row(modifier = Modifier.clickable { onThemeChanged(!isDarkTheme) }, verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isDarkTheme) ImageVector.vectorResource(id = R.drawable.light_mode_24dp_5f6368_fill0_wght400_grad0_opsz24) else ImageVector.vectorResource(id = R.drawable.dark_mode_24dp_5f6368_fill0_wght400_grad0_opsz24),
                    contentDescription = "Theme", tint = textColor
                )
                Text(text = if (isDarkTheme) "Light" else "Dark", color = textColor, modifier = Modifier.padding(start = 4.dp))
            }
            // Language Toggle
            Row(modifier = Modifier.clickable { onLanguageChanged(!isEnglish) }, verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.language_24dp_5f6368_fill0_wght400_grad0_opsz24), contentDescription = "Lang", tint = textColor)
                Text(text = if (isEnglish) "বাংলা" else "English", color = textColor, modifier = Modifier.padding(start = 4.dp))
            }
        }

        // Search Bar with Filter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
                .background(
                    color = if (isDarkTheme) Color.DarkGray else Color.White,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))

                // Text Field
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.text.isEmpty()) {
                        Text(
                            text = if (isEnglish) "Search..." else "অনুসন্ধান করুন...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        cursorBrush = if (isDarkTheme) Brush.verticalGradient(listOf(Color.White, Color.White)) else Brush.verticalGradient(listOf(Color.Black, Color.Black)),
                        textStyle = LocalTextStyle.current.copy(color = textColor, fontSize = 14.sp)
                    )
                }

                // Clear Button
                if (searchQuery.text.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = TextFieldValue("") }, modifier = Modifier.size(18.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // FILTER ICON & DROPDOWN
                Box {
                    IconButton(onClick = { showFilterMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.List, // Or use a Filter icon if available
                            contentDescription = "Filter",
                            tint = if (selectedStateFilter != null) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All States") },
                            onClick = {
                                selectedStateFilter = null
                                showFilterMenu = false
                            }
                        )
                        ElementState.values().forEach { state ->
                            DropdownMenuItem(
                                text = { Text(state.displayName) },
                                onClick = {
                                    selectedStateFilter = state
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Elements List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredElements) { element ->
                ElementCard(
                    element = element,
                    isDarkTheme = isDarkTheme,
                    isEnglish = isEnglish,
                    onClick = { selectedElement ->
                        navController.navigate("elementDetail/${selectedElement.atomicNumber}")
                    }
                )
            }
        }
    }
}