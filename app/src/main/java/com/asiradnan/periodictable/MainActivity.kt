package com.asiradnan.periodictable

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.asiradnan.periodictable.ui.theme.PeriodicTableTheme

object ThemePreference {
    private const val PREF_NAME = "AppPreferences"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_LANGUAGE = "language"

    fun saveDarkMode(context: Context, isDarkMode: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply()
    }

    fun getDarkMode(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    fun saveLanguage(context: Context, isEnglish: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_LANGUAGE, isEnglish).apply()
    }

    fun getLanguage(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_LANGUAGE, true)
    }
}

// NumberTranslator.kt
object NumberTranslator {
    private val englishToBanglaMap = mapOf(
        '0' to '০',
        '1' to '১',
        '2' to '২',
        '3' to '৩',
        '4' to '৪',
        '5' to '৫',
        '6' to '৬',
        '7' to '৭',
        '8' to '৮',
        '9' to '৯'
    )

    fun translateToBangla(number: String): String {
        return number.map { char ->
            englishToBanglaMap[char] ?: char
        }.joinToString("")
    }
}


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
//            WindowCompat.getInsetsController(window, window.decorView).apply {
//                isAppearanceLightStatusBars = !isDarkTheme
//            }

            PeriodicTableTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                                        .systemBarsPadding(), // Add system bars padding

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
//                                    WindowCompat.getInsetsController(window, window.decorView)
//                                        .apply {
//                                            isAppearanceLightStatusBars = !newTheme
//                                        }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChemicalElementsScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    isEnglish: Boolean,
    onLanguageChanged: (Boolean) -> Unit
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


    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    // Set system UI colors


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(color = backgroundColor)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // Top Bar with Theme and Language Toggle
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
                    fontSize = 14.sp,

                    )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Elements List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
        val screenHeight = maxHeight

        // Adjust sizes and spacing based on screen size
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
                    .shadow(
                        8.dp,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(
                        color = if (isDarkTheme) Color.DarkGray.copy(alpha = 0.4f) else Color.White,
                        shape = RoundedCornerShape(24.dp)
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
        }
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
fun toBanglaLabel(label: String): String {
    when (label) {
        ("Kind") -> return "প্রকার"
        ("Atomic Mass") -> return "পারমাণবিক ভর"
        ("Group") -> return "গ্রুপ"
        ("Period") -> return "পর্যায়"
        ("Protons") -> return "প্রোটন"
        ("Electrons") -> return "ইলেকট্রন"
        ("State") -> return "অবস্থা"
        ("Electronegativity") -> return "তড়িৎঋণাত্মকতা"
        ("Neutrons") -> return "নিউট্রন"
//        ("Liquid") -> return "তরল"
    }
    return label
}

fun toBanglaState(state: String): String {
    when (state) {
        ("Gas") -> return "বায়বীয়"
        ("Solid") -> return "কঠিন"
        ("Liquid") -> return "তরল"
    }
    return "অজ্ঞাত"
}

fun toBanglaKind(kind: String): String {
    when (kind) {
        ("Nonmetal") -> return banglaKinds[6]
        ("Noble Gas") -> return banglaKinds[8]
        ("Alkali Metal") -> return banglaKinds[0]
        ("Halogen") -> return banglaKinds[7]
        ("Alkaline Earth Metal") -> return banglaKinds[1]
        ("Metalloid") -> return banglaKinds[5]
        ("Lanthanide") -> return banglaKinds[2]
        ("Actinide") -> return banglaKinds[3]
        ("Transition Metal") -> return banglaKinds[4]

    }
    return "Post Transition Metal"
}

val banglaNames = listOf(
    "হাইড্রোজেন",
    "হিলিয়াম",
    "লিথিয়াম",
    "বেরিলিয়াম",
    "বোরন",
    "কার্বন",
    "নাইট্রোজেন",
    "অক্সিজেন",
    "ফ্লোরিন",
    "নিয়ন",
    "সোডিয়াম",
    "ম্যাগনেসিয়াম",
    "অ্যালুমিনিয়াম",
    "সিলিকন",
    "ফসফরাস",
    "সালফার",
    "ক্লোরিন",
    "আর্গন",
    "পটাশিয়াম",
    "ক্যালসিয়াম",
    "স্ক্যান্ডিয়াম",
    "টাইটানিয়াম",
    "ভ্যানাডিয়াম",
    "ক্রোমিয়াম",
    "ম্যাঙ্গানিজ",
    "আয়রন",
    "কোবাল্ট",
    "নিকেল",
    "কপার",
    "জিংক",
    "গ্যালিয়াম",
    "জার্মেনিয়াম",
    "আর্সেনিক",
    "সেলেনিয়াম",
    "ব্রোমিন",
    "ক্রিপ্টন",
    "রুবিডিয়াম",
    "স্ট্রনশিয়াম",
    "ইট্রিয়াম",
    "জিরকোনিয়াম",
    "নাইওবিয়াম",
    "মলিবডেনাম",
    "টেকনেশিয়াম",
    "রুথেনিয়াম",
    "রোডিয়াম",
    "প্যালাডিয়াম",
    "রূপা",
    "ক্যাডমিয়াম",
    "ইন্ডিয়াম",
    "টিন",
    "অ্যান্টিমনি",
    "টেলুরিয়াম",
    "আয়োডিন",
    "জেনন",
    "সিজিয়াম",
    "বেরিয়াম",
    "ল্যান্থানাম",
    "সিরিয়াম",
    "প্রাসিওডিমিয়াম",
    "নিওডিমিয়াম",
    "প্রমিথিয়াম",
    "স্যামারিয়াম",
    "ইউরোপিয়াম",
    "গ্যাডোলিনিয়াম",
    "টার্বিয়াম",
    "ডিস্প্রোসিয়াম",
    "হলমিয়াম",
    "ইরবিয়াম",
    "থুলিয়াম",
    "ইটারবিয়াম",
    "লুটেশিয়াম",
    "হাফনিয়াম",
    "ট্যানটালাম",
    "টাংস্টেন",
    "রেনিয়াম",
    "অসমিয়াম",
    "ইরিডিয়াম",
    "প্লাটিনাম",
    "সোনা",
    "পারদ",
    "থ্যালিয়াম",
    "সীসা",
    "বিসমাথ",
    "পোলোনিয়াম",
    "অ্যাস্টাটিন",
    "রেডন",
    "ফ্রান্সিয়াম",
    "রেডিয়াম",
    "অ্যাক্টিনিয়াম",
    "থোরিয়াম",
    "প্রোট্যাক্টিনিয়াম",
    "ইউরেনিয়াম",
    "নেপচুনিয়াম",
    "প্লুটোনিয়াম",
    "আমেরিসিয়াম",
    "কুরিয়াম",
    "বার্কেলিয়াম",
    "ক্যালিফোর্নিয়াম",
    "আইনস্টাইনিয়াম",
    "ফার্মিয়াম",
    "মেন্ডেলিভিয়াম",
    "নোবেলিয়াম",
    "লরেন্সিয়াম",
    "রাদারফোর্ডিয়াম",
    "ডুবনিয়াম",
    "সিবোর্গিয়াম",
    "বোহরিয়াম",
    "হ্যাসিয়াম",
    "মেইটনেরিয়াম",
    "ডার্মস্টাটিয়াম",
    "রন্টজেনিয়াম",
    "কোপার্নিসিয়াম",
    "নিহোনিয়াম",
    "ফ্লেরোভিয়াম",
    "মস্কোভিয়াম",
    "লিভারমোরিয়াম",
    "টেনেসাইন",
    "অগানেসন",
)
val banglaKinds = listOf(
    "ক্ষার ধাতু",
    "মৃৎক্ষার ধাতু",
    "ল্যান্থানাইড",
    "অ্যাক্টিনাইড",
    "অবস্থান্তর ধাতু",
    "ধাতুকল্প",
    "অন্যান্য অধাতু",
    "হ্যালোজেন",
    "নিষ্ক্রিয় গ্যাস",
    "অন্যান্য ধাতু"
)
val elements = listOf(
    (Element(
        name = "Hydrogen",
        symbol = "H",
        atomicNumber = 1,
        atomicMass = 1.008,
        kind = "Nonmetal",
        state = "Gas",
        period = 1,
        group = 1,
        electronegativity = 2.2,
        electronConfiguration = "1s1"
    )),
    (Element(
        name = "Helium",
        symbol = "He",
        atomicNumber = 2,
        atomicMass = 4.002,
        kind = "Noble Gas",
        state = "Gas",
        period = 1,
        group = 18,
        electronegativity = null,
        electronConfiguration = "1s2"
    )),
    (Element(
        name = "Lithium",
        symbol = "Li",
        atomicNumber = 3,
        atomicMass = 6.941,
        kind = "Alkali Metal",
        state = "Solid",
        period = 2,
        group = 1,
        electronegativity = 0.98,
        electronConfiguration = "1s2 2s1"
    )),
    (Element(
        name = "Beryllium",
        symbol = "Be",
        atomicNumber = 4,
        atomicMass = 9.012,
        kind = "Alkaline Earth Metal",
        state = "Solid",
        period = 2,
        group = 2,
        electronegativity = 1.57,
        electronConfiguration = "1s2 2s2"
    )),
    (Element(
        name = "Boron",
        symbol = "B",
        atomicNumber = 5,
        atomicMass = 10.811,
        kind = "Metalloid",
        state = "Solid",
        period = 2,
        group = 13,
        electronegativity = 2.04,
        electronConfiguration = "1s2 2s2 2p1"
    )),
    (Element(
        name = "Carbon",
        symbol = "C",
        atomicNumber = 6,
        atomicMass = 12.011,
        kind = "Nonmetal",
        state = "Solid",
        period = 2,
        group = 14,
        electronegativity = 2.55,
        electronConfiguration = "1s2 2s2 2p2"
    )),
    (Element(
        name = "Nitrogen",
        symbol = "N",
        atomicNumber = 7,
        atomicMass = 14.007,
        kind = "Nonmetal",
        state = "Gas",
        period = 2,
        group = 15,
        electronegativity = 3.04,
        electronConfiguration = "1s2 2s2 2p3"
    )),
    (Element(
        name = "Oxygen",
        symbol = "O",
        atomicNumber = 8,
        atomicMass = 15.999,
        kind = "Nonmetal",
        state = "Gas",
        period = 2,
        group = 16,
        electronegativity = 3.44,
        electronConfiguration = "1s2 2s2 2p4"
    )),
    (Element(
        name = "Fluorine",
        symbol = "F",
        atomicNumber = 9,
        atomicMass = 18.998,
        kind = "Halogen",
        state = "Gas",
        period = 2,
        group = 17,
        electronegativity = 3.98,
        electronConfiguration = "1s2 2s2 2p5"
    )),
    (Element(
        name = "Neon",
        symbol = "Ne",
        atomicNumber = 10,
        atomicMass = 20.18,
        kind = "Noble Gas",
        state = "Gas",
        period = 2,
        group = 18,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6"
    )),
    (Element(
        name = "Sodium",
        symbol = "Na",
        atomicNumber = 11,
        atomicMass = 22.99,
        kind = "Alkali Metal",
        state = "Solid",
        period = 3,
        group = 1,
        electronegativity = 0.93,
        electronConfiguration = "1s2 2s2 2p6 3s1"
    )),
    (Element(
        name = "Magnesium",
        symbol = "Mg",
        atomicNumber = 12,
        atomicMass = 24.305,
        kind = "Alkaline Earth Metal",
        state = "Solid",
        period = 3,
        group = 2,
        electronegativity = 1.31,
        electronConfiguration = "1s2 2s2 2p6 3s2"
    )),
    (Element(
        name = "Aluminum",
        symbol = "Al",
        atomicNumber = 13,
        atomicMass = 26.982,
        kind = "Post Transition Metal",
        state = "Solid",
        period = 3,
        group = 13,
        electronegativity = 1.61,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p1"
    )),
    (Element(
        name = "Silicon",
        symbol = "Si",
        atomicNumber = 14,
        atomicMass = 28.086,
        kind = "Metalloid",
        state = "Solid",
        period = 3,
        group = 14,
        electronegativity = 1.9,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p2"
    )),
    (Element(
        name = "Phosphorus",
        symbol = "P",
        atomicNumber = 15,
        atomicMass = 30.974,
        kind = "Nonmetal",
        state = "Solid",
        period = 3,
        group = 15,
        electronegativity = 2.19,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p3"
    )),
    (Element(
        name = "Sulfur",
        symbol = "S",
        atomicNumber = 16,
        atomicMass = 32.065,
        kind = "Nonmetal",
        state = "Solid",
        period = 3,
        group = 16,
        electronegativity = 2.58,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p4"
    )),
    (Element(
        name = "Chlorine",
        symbol = "Cl",
        atomicNumber = 17,
        atomicMass = 35.453,
        kind = "Halogen",
        state = "Gas",
        period = 1,
        group = 17,
        electronegativity = 3.16,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p5"
    )),
    (Element(
        name = "Argon",
        symbol = "Ar",
        atomicNumber = 18,
        atomicMass = 39.948,
        kind = "Noble Gas",
        state = "Gas",
        period = 3,
        group = 18,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6"
    )),
    (Element(
        name = "Potassium",
        symbol = "K",
        atomicNumber = 19,
        atomicMass = 39.098,
        kind = "Alkali Metal",
        state = "Solid",
        period = 4,
        group = 1,
        electronegativity = 0.82,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 4s1"
    )),
    (Element(
        name = "Calcium",
        symbol = "Ca",
        atomicNumber = 20,
        atomicMass = 40.078,
        kind = "Alkaline Earth Metal",
        state = "Solid",
        period = 4,
        group = 2,
        electronegativity = 1.0,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 4s2"
    )),
    (Element(
        name = "Scandium",
        symbol = "Sc",
        atomicNumber = 21,
        atomicMass = 44.956,
        kind = "Transition Metal",
        state = "Solid",
        period = 4,
        group = 3,
        electronegativity = 1.36,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d1 4s2"
    )),
    (Element(
        name = "Titanium",
        symbol = "Ti",
        atomicNumber = 22,
        atomicMass = 47.867,
        kind = "Transition Metal",
        state = "Solid",
        period = 4,
        group = 4,
        electronegativity = 1.54,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d2 4s2"
    )),
    (Element(
        name = "Vanadium",
        symbol = "V",
        atomicNumber = 23,
        atomicMass = 50.942,
        kind = "Transition Metal",
        state = "Solid",
        period = 4,
        group = 5,
        electronegativity = 1.63,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d3 4s2"
    )),
    (Element(
        name = "Chromium",
        symbol = "Cr",
        atomicNumber = 24,
        atomicMass = 51.996,
        kind = "Transition Metal",
        state = "Solid",
        period = 4,
        group = 6,
        electronegativity = 1.66,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d5 4s1"
    )),
    (Element(
        name = "Manganese",
        symbol = "Mn",
        atomicNumber = 25,
        atomicMass = 54.938,
        kind = "Transition Metal",
        state = "Solid",
        period = 4,
        group = 7,
        electronegativity = 1.55,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d5 4s2"
    )),
    (Element(
        name = "Iron",
        symbol = "Fe",
        atomicNumber = 26,
        atomicMass = 55.845,
        kind = "Transition Metal",
        state = "Solid",
        period = 4,
        group = 8,
        electronegativity = 1.83,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d6 4s2"
    )),
    (Element(
        name = "Cobalt",
        symbol = "Co",
        atomicNumber = 27,
        atomicMass = 58.933,
        kind = "Transition Metal",
        state = "Solid",
        period = 4,
        group = 9,
        electronegativity = 1.88,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d7 4s2"
    )),
    (Element(
        name = "Nickel",
        symbol = "Ni",
        atomicNumber = 28,
        atomicMass = 58.693,
        kind = "Transition Metal",
        state = "Solid",
        period = 4,
        group = 10,
        electronegativity = 1.91,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d8 4s2"
    )),
    (Element(
        name = "Copper",
        symbol = "Cu",
        atomicNumber = 29,
        atomicMass = 63.546,
        kind = "Transition Metal",
        state = "Solid",
        period = 4,
        group = 11,
        electronegativity = 1.9,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s1"
    )),
    (Element(
        name = "Zinc",
        symbol = "Zn",
        atomicNumber = 30,
        atomicMass = 65.38,
        kind = "Transition Metal",
        state = "Solid",
        period = 4,
        group = 12,
        electronegativity = 1.65,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d1 4s2"
    )),
    (Element(
        name = "Gallium",
        symbol = "Ga",
        atomicNumber = 31,
        atomicMass = 69.723,
        kind = "Post Transition Metal",
        state = "Solid",
        period = 4,
        group = 13,
        electronegativity = 1.81,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p1"
    )),
    (Element(
        name = "Germanium",
        symbol = "Ge",
        atomicNumber = 32,
        atomicMass = 72.64,
        kind = "Metalloid",
        state = "Solid",
        period = 4,
        group = 14,
        electronegativity = 2.01,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p2"
    )),
    (Element(
        name = "Arsenic",
        symbol = "As",
        atomicNumber = 33,
        atomicMass = 74.922,
        kind = "Metalloid",
        state = "Solid",
        period = 4,
        group = 15,
        electronegativity = 2.18,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p3"
    )),
    (Element(
        name = "Selenium",
        symbol = "Se",
        atomicNumber = 34,
        atomicMass = 78.96,
        kind = "Nonmetal",
        state = "Solid",
        period = 4,
        group = 16,
        electronegativity = 2.55,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p4"
    )),
    (Element(
        name = "Bromine",
        symbol = "Br",
        atomicNumber = 35,
        atomicMass = 79.904,
        kind = "Halogen",
        state = "Liquid",
        period = 4,
        group = 17,
        electronegativity = 2.96,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p5"
    )),
    (Element(
        name = "Krypton",
        symbol = "Kr",
        atomicNumber = 36,
        atomicMass = 83.798,
        kind = "Noble Gas",
        state = "Gas",
        period = 4,
        group = 18,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6"
    )),
    (Element(
        name = "Rubidium",
        symbol = "Rb",
        atomicNumber = 37,
        atomicMass = 85.468,
        kind = "Alkali Metal",
        state = "Solid",
        period = 5,
        group = 1,
        electronegativity = 0.82,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 5s1"
    )),
    (Element(
        name = "Strontium",
        symbol = "Sr",
        atomicNumber = 38,
        atomicMass = 87.62,
        kind = "Alkaline Earth Metal",
        state = "Solid",
        period = 5,
        group = 2,
        electronegativity = 0.95,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 5s2"
    )),
    (Element(
        name = "Yttrium",
        symbol = "Y",
        atomicNumber = 39,
        atomicMass = 88.906,
        kind = "Transition Metal",
        state = "Solid",
        period = 5,
        group = 3,
        electronegativity = 1.22,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d1 5s2"
    )),
    (Element(
        name = "Zirconium",
        symbol = "Zr",
        atomicNumber = 40,
        atomicMass = 91.224,
        kind = "Transition Metal",
        state = "Solid",
        period = 5,
        group = 4,
        electronegativity = 1.33,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d2 5s2"
    )),
    (Element(
        name = "Niobium",
        symbol = "Nb",
        atomicNumber = 41,
        atomicMass = 98.906,
        kind = "Transition Metal",
        state = "Solid",
        period = 5,
        group = 5,
        electronegativity = 1.6,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d4 5s1"
    )),
    (Element(
        name = "Molybdenum",
        symbol = "Mo",
        atomicNumber = 42,
        atomicMass = 95.96,
        kind = "Transition Metal",
        state = "Solid",
        period = 5,
        group = 6,
        electronegativity = 2.16,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d5 5s1"
    )),
    (Element(
        name = "Technetium",
        symbol = "Tc",
        atomicNumber = 43,
        atomicMass = 98.0,
        kind = "Transition Metal",
        state = "Solid",
        period = 5,
        group = 7,
        electronegativity = 1.9,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d5 5s2"
    )),
    (Element(
        name = "Ruthenium",
        symbol = "Ru",
        atomicNumber = 44,
        atomicMass = 101.107,
        kind = "Transition Metal",
        state = "Solid",
        period = 5,
        group = 8,
        electronegativity = 2.2,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d7 5s1"
    )),
    (Element(
        name = "Rhodium",
        symbol = "Rh",
        atomicNumber = 45,
        atomicMass = 102.906,
        kind = "Transition Metal",
        state = "Solid",
        period = 5,
        group = 9,
        electronegativity = 2.28,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d8 5s1"
    )),
    (Element(
        name = "Palladium",
        symbol = "Pd",
        atomicNumber = 46,
        atomicMass = 106.42,
        kind = "Transition Metal",
        state = "Solid",
        period = 5,
        group = 10,
        electronegativity = 2.2,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10"
    )),
    (Element(
        name = "Silver",
        symbol = "Ag",
        atomicNumber = 47,
        atomicMass = 7.869,
        kind = "Transition Metal",
        state = "Solid",
        period = 5,
        group = 11,
        electronegativity = 1.93,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s1"
    )),
    (Element(
        name = "Cadmium",
        symbol = "Cd",
        atomicNumber = 48,
        atomicMass = 112.411,
        kind = "Transition Metal",
        state = "Solid",
        period = 5,
        group = 12,
        electronegativity = 1.69,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2"
    )),
    (Element(
        name = "Indium",
        symbol = "In",
        atomicNumber = 49,
        atomicMass = 114.818,
        kind = "Post Transition Metal",
        state = "Solid",
        period = 5,
        group = 13,
        electronegativity = 1.78,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p1"
    )),
    (Element(
        name = "Tin",
        symbol = "Sn",
        atomicNumber = 50,
        atomicMass = 118.71,
        kind = "Post Transition Metal",
        state = "Solid",
        period = 1,
        group = 14,
        electronegativity = 1.96,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p2"
    )),
    (Element(
        name = "Antimony",
        symbol = "Sb",
        atomicNumber = 51,
        atomicMass = 121.76,
        kind = "Metalloid",
        state = "Solid",
        period = 5,
        group = 15,
        electronegativity = 2.05,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p3"
    )),
    (Element(
        name = "Tellurium",
        symbol = "Te",
        atomicNumber = 52,
        atomicMass = 127.6,
        kind = "Metalloid",
        state = "Solid",
        period = 5,
        group = 16,
        electronegativity = 2.1,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p4"
    )),
    (Element(
        name = "Iodine",
        symbol = "I",
        atomicNumber = 53,
        atomicMass = 126.904,
        kind = "Halogen",
        state = "Solid",
        period = 5,
        group = 17,
        electronegativity = 2.66,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p5"
    )),
    (Element(
        name = "Xenon",
        symbol = "Xe",
        atomicNumber = 54,
        atomicMass = 131.293,
        kind = "Noble Gas",
        state = "Gas",
        period = 5,
        group = 18,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p6"
    )),
    (Element(
        name = "Caesium",
        symbol = "Cs",
        atomicNumber = 55,
        atomicMass = 132.905,
        kind = "Alkali Metal",
        state = "Solid",
        period = 6,
        group = 1,
        electronegativity = 0.79,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p6 6s1"
    )),
    (Element(
        name = "Barium",
        symbol = "Ba",
        atomicNumber = 56,
        atomicMass = 137.327,
        kind = "Alkaline Earth Metal",
        state = "Solid",
        period = 6,
        group = 2,
        electronegativity = 0.89,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p6 6s2"
    )),
    (Element(
        name = "Lanthanum",
        symbol = "La",
        atomicNumber = 57,
        atomicMass = 138.905,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.1,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p6 5d1 6s2"
    )),
    (Element(
        name = "Cerium",
        symbol = "Ce",
        atomicNumber = 58,
        atomicMass = 140.116,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.12,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f1 5s2 5p6 5d1 6s2"
    )),
    (Element(
        name = "Praseodymium",
        symbol = "Pr",
        atomicNumber = 59,
        atomicMass = 140.908,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.13,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f3 5s2 5p6 6s2"
    )),
    (Element(
        name = "Neodymium",
        symbol = "Nd",
        atomicNumber = 60,
        atomicMass = 144.242,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.14,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f4 5s2 5p6 6s2"
    )),
    (Element(
        name = "Promethium",
        symbol = "Pm",
        atomicNumber = 61,
        atomicMass = 145.0,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.13,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f5 5s2 5p6 6s2"
    )),
    (Element(
        name = "Samarium",
        symbol = "Sm",
        atomicNumber = 62,
        atomicMass = 150.36,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.17,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f6 5s2 5p6 6s2"
    )),
    (Element(
        name = "Europium",
        symbol = "Eu",
        atomicNumber = 63,
        atomicMass = 151.964,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.2,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f7 5s2 5p6 6s2"
    )),
    (Element(
        name = "Gadolinium",
        symbol = "Gd",
        atomicNumber = 64,
        atomicMass = 157.25,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.2,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f7 5s2 5p6 5d1 6s2"
    )),
    (Element(
        name = "Terbium",
        symbol = "Tb",
        atomicNumber = 65,
        atomicMass = 158.925,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.2,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f9 5s2 5p6 6s2"
    )),
    (Element(
        name = "Dysprosium",
        symbol = "Dy",
        atomicNumber = 66,
        atomicMass = 162.5,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.22,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f10 5s2 5p6 6s2"
    )),
    (Element(
        name = "Holmium",
        symbol = "Ho",
        atomicNumber = 67,
        atomicMass = 164.93,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.23,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f11 5s2 5p6 6s2"
    )),
    (Element(
        name = "Erbium",
        symbol = "Er",
        atomicNumber = 68,
        atomicMass = 167.259,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.24,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f12 5s2 5p6 6s2"
    )),
    (Element(
        name = "Thulium",
        symbol = "Tm",
        atomicNumber = 69,
        atomicMass = 168.934,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.25,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f13 5s2 5p6 6s2"
    )),
    (Element(
        name = "Ytterbium",
        symbol = "Yb",
        atomicNumber = 70,
        atomicMass = 173.054,
        kind = "Lanthanide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.1,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 6s2"
    )),
    (Element(
        name = "Lutetium",
        symbol = "Lu",
        atomicNumber = 71,
        atomicMass = 174.967,
        kind = "Lanthanide",
        state = "Solid",
        period = 6,
        group = 18,
        electronegativity = 1.27,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d1 6s2"
    )),
    (Element(
        name = "Hafnium",
        symbol = "Hf",
        atomicNumber = 72,
        atomicMass = 178.49,
        kind = "Transition Metal",
        state = "Solid",
        period = 6,
        group = 4,
        electronegativity = 1.3,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d2 6s2"
    )),
    (Element(
        name = "Tantalum",
        symbol = "Ta",
        atomicNumber = 73,
        atomicMass = 180.948,
        kind = "Transition Metal",
        state = "Solid",
        period = 6,
        group = 5,
        electronegativity = 1.5,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d3 6s2"
    )),
    (Element(
        name = "Tungsten",
        symbol = "W",
        atomicNumber = 74,
        atomicMass = 183.84,
        kind = "Transition Metal",
        state = "Solid",
        period = 6,
        group = 6,
        electronegativity = 2.36,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d4 6s2"
    )),
    (Element(
        name = "Rhenium",
        symbol = "Re",
        atomicNumber = 75,
        atomicMass = 186.207,
        kind = "Transition Metal",
        state = "Solid",
        period = 6,
        group = 7,
        electronegativity = 1.9,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d5 6s2"
    )),
    (Element(
        name = "Osmium",
        symbol = "Os",
        atomicNumber = 76,
        atomicMass = 190.23,
        kind = "Transition Metal",
        state = "Solid",
        period = 6,
        group = 8,
        electronegativity = 2.2,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d6 6s2"
    )),
    (Element(
        name = "Iridium",
        symbol = "Ir",
        atomicNumber = 77,
        atomicMass = 192.217,
        kind = "Transition Metal",
        state = "Solid",
        period = 6,
        group = 9,
        electronegativity = 2.2,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d7 6s2"
    )),
    (Element(
        name = "Platinum",
        symbol = "Pt",
        atomicNumber = 78,
        atomicMass = 195.084,
        kind = "Transition Metal",
        state = "Solid",
        period = 6,
        group = 10,
        electronegativity = 2.28,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d9 6s1"
    )),
    (Element(
        name = "Gold",
        symbol = "Au",
        atomicNumber = 79,
        atomicMass = 196.967,
        kind = "Transition Metal",
        state = "Solid",
        period = 6,
        group = 11,
        electronegativity = 2.54,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s1"
    )),
    (Element(
        name = "Mercury",
        symbol = "Hg",
        atomicNumber = 80,
        atomicMass = 200.59,
        kind = "Transition Metal",
        state = "Liquid",
        period = 6,
        group = 12,
        electronegativity = 2.0,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2"
    )),
    (Element(
        name = "Thallium",
        symbol = "Tl",
        atomicNumber = 81,
        atomicMass = 204.383,
        kind = "Actinide",
        state = "Solid",
        period = 6,
        group = 13,
        electronegativity = 2.04,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p1"
    )),
    (Element(
        name = "Lead",
        symbol = "Pb",
        atomicNumber = 82,
        atomicMass = 207.2,
        kind = "Actinide",
        state = "Solid",
        period = 6,
        group = 14,
        electronegativity = 2.33,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p2"
    )),
    (Element(
        name = "Bismuth",
        symbol = "Bi",
        atomicNumber = 83,
        atomicMass = 208.98,
        kind = "Actinide",
        state = "Solid",
        period = 6,
        group = 15,
        electronegativity = 2.02,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p3"
    )),
    (Element(
        name = "Polonium",
        symbol = "Po",
        atomicNumber = 84,
        atomicMass = 210.0,
        kind = "Actinide",
        state = "Solid",
        period = 6,
        group = 16,
        electronegativity = 2.0,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p4"
    )),
    (Element(
        name = "Astatine",
        symbol = "At",
        atomicNumber = 85,
        atomicMass = 210.0,
        kind = "Halogen",
        state = "Solid",
        period = 6,
        group = 17,
        electronegativity = 2.2,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p5"
    )),
    (Element(
        name = "Radon",
        symbol = "Rn",
        atomicNumber = 86,
        atomicMass = 222.0,
        kind = "Noble Gas",
        state = "Gas",
        period = 6,
        group = 18,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p6"
    )),
    (Element(
        name = "Francium",
        symbol = "Fr",
        atomicNumber = 87,
        atomicMass = 223.0,
        kind = "Alkali Metal",
        state = "Solid",
        period = 7,
        group = 1,
        electronegativity = 0.7,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s24p6 4d10 4f14 5s2 5p6 5d10 6s2 6p6 7s1"
    )),
    (Element(
        name = "Radium",
        symbol = "Ra",
        atomicNumber = 88,
        atomicMass = 226.0,
        kind = "Alkaline Earth Metal",
        state = "Solid",
        period = 7,
        group = 2,
        electronegativity = 0.9,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p6 7s2"
    )),
    (Element(
        name = "Actinium",
        symbol = "Ac",
        atomicNumber = 89,
        atomicMass = 227.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.1,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p6 6d1 7s2"
    )),
    (Element(
        name = "Thorium",
        symbol = "Th",
        atomicNumber = 90,
        atomicMass = 232.038,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.3,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p6 6d2 7s2"
    )),
    (Element(
        name = "Protactinium",
        symbol = "Pa",
        atomicNumber = 91,
        atomicMass = 231.036,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.5,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f2 6s2 6p6 6d1 7s2"
    )),
    (Element(
        name = "Uranium",
        symbol = "U",
        atomicNumber = 92,
        atomicMass = 238.029,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.38,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f3 6s2 6p6 6d1 7s2"
    )),
    (Element(
        name = "Neptunium",
        symbol = "Np",
        atomicNumber = 93,
        atomicMass = 237.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.36,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f4 6s2 6p6 6d1 7s2"
    )),
    (Element(
        name = "Plutonium",
        symbol = "Pu",
        atomicNumber = 94,
        atomicMass = 244.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.28,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f6 6s2 6p6 7s2"
    )),
    (Element(
        name = "Americium",
        symbol = "Am",
        atomicNumber = 95,
        atomicMass = 243.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.3,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f7 6s2 6p6 7s2"
    )),
    (Element(
        name = "Curium",
        symbol = "Cm",
        atomicNumber = 96,
        atomicMass = 247.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.3,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f7 6s2 6p6 6d1 7s2"
    )),
    (Element(
        name = "Berkelium",
        symbol = "Bk",
        atomicNumber = 97,
        atomicMass = 247.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.3,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f9 6s2 6p6 7s2"
    )),
    (Element(
        name = "Californium",
        symbol = "Cf",
        atomicNumber = 98,
        atomicMass = 251.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.3,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f10 6s2 6p6 7s2"
    )),
    (Element(
        name = "Einsteinium",
        symbol = "Es",
        atomicNumber = 99,
        atomicMass = 252.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.3,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f11 6s2 6p6 7s2"
    )),
    (Element(
        name = "Fermium",
        symbol = "Fm",
        atomicNumber = 100,
        atomicMass = 257.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.3,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f12 6s2 6p6 7s2"
    )),
    (Element(
        name = "Mendelevium",
        symbol = "Md",
        atomicNumber = 101,
        atomicMass = 258.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.3,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f13 6s2 6p6 7s2"
    )),
    (Element(
        name = "Nobelium",
        symbol = "No",
        atomicNumber = 102,
        atomicMass = 259.0,
        kind = "Actinide",
        state = "Solid",
        period = null,
        group = null,
        electronegativity = 1.3,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 7s2"
    )),
    (Element(
        name = "Lawrencium",
        symbol = "Lr",
        atomicNumber = 103,
        atomicMass = 262.0,
        kind = "Actinide",
        state = "Solid",
        period = 7,
        group = 18,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 7s2 7p1"
    )),
    (Element(
        name = "Rutherfordium",
        symbol = "Rf",
        atomicNumber = 104,
        atomicMass = 261.0,
        kind = "Transition Metal",
        state = "Solid",
        period = 7,
        group = 4,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d2 7s2"
    )),
    (Element(
        name = "Dubnium",
        symbol = "Db",
        atomicNumber = 105,
        atomicMass = 262.0,
        kind = "Transition Metal",
        state = "Solid",
        period = 7,
        group = 5,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d3 7s2"
    )),
    (Element(
        name = "Seaborgium",
        symbol = "Sg",
        atomicNumber = 106,
        atomicMass = 266.0,
        kind = "Transition Metal",
        state = "Solid",
        period = 7,
        group = 6,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d4 7s2"
    )),
    (Element(
        name = "Bohrium",
        symbol = "Bh",
        atomicNumber = 107,
        atomicMass = 264.0,
        kind = "Transition Metal",
        state = "Solid",
        period = 7,
        group = 7,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d5 7s2"
    )),
    (Element(
        name = "Hassium",
        symbol = "Hs",
        atomicNumber = 108,
        atomicMass = 267.0,
        kind = "Transition Metal",
        state = "Solid",
        period = 7,
        group = 8,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d6 7s2"
    )),
    (Element(
        name = "Meitnerium",
        symbol = "Mt",
        atomicNumber = 109,
        atomicMass = 268.0,
        kind = "Transition Metal",
        state = "Solid",
        period = 7,
        group = 9,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d7 7s2"
    )),
    (Element(
        name = "Darmstadtium",
        symbol = "Ds",
        atomicNumber = 110,
        atomicMass = 271.0,
        kind = "Transition Metal",
        state = "Solid",
        period = 7,
        group = 10,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d8 7s2"
    )),
    (Element(
        name = "Roentgenium",
        symbol = "Rg",
        atomicNumber = 111,
        atomicMass = 272.0,
        kind = "Transition Metal",
        state = "Solid",
        period = 7,
        group = 11,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d9 7s2"
    )),
    (Element(
        name = "Copernicium",
        symbol = "Cn",
        atomicNumber = 112,
        atomicMass = 285.0,
        kind = "Transition Metal",
        state = "Solid",
        period = 7,
        group = 12,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2"
    )),
    (Element(
        name = "Nihonium",
        symbol = "Nh",
        atomicNumber = 113,
        atomicMass = 284.0,
        kind = "Post Transition Metal",
        state = "Solid",
        period = 7,
        group = 13,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p1"
    )),
    (Element(
        name = "Flerovium",
        symbol = "Fl",
        atomicNumber = 114,
        atomicMass = 289.0,
        kind = "Post Transition Metal",
        state = "Solid",
        period = 7,
        group = 14,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p2"
    )),
    (Element(
        name = "Moscovium",
        symbol = "Mc",
        atomicNumber = 115,
        atomicMass = 288.0,
        kind = "Post Transition Metal",
        state = "Solid",
        period = 7,
        group = 15,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p3"
    )),
    (Element(
        name = "Livermorium",
        symbol = "Lv",
        atomicNumber = 116,
        atomicMass = 292.0,
        kind = "Post Transition Metal",
        state = "Solid",
        period = 7,
        group = 16,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p4"
    )),
    (Element(
        name = "Tennessine",
        symbol = "Ts",
        atomicNumber = 117,
        atomicMass = 295.0,
        kind = "Halogen",
        state = "Solid",
        period = 7,
        group = 17,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p5"
    )),
    (Element(
        name = "Oganesson",
        symbol = "Og",
        atomicNumber = 118,
        atomicMass = 294.0,
        kind = "Noble Gas",
        state = "Gas",
        period = 7,
        group = 18,
        electronegativity = null,
        electronConfiguration = "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p6"
    ))
)

