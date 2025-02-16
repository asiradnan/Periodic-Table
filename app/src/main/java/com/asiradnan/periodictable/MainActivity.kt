package com.asiradnan.periodictable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.asiradnan.periodictable.ui.theme.PeriodicTableTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PeriodicTableTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "chemicalElements") {
                    composable("chemicalElements") {
                        ChemicalElementsScreen(navController)
                    }
                    composable("elementDetail/{atomicNumber}") { backStackEntry ->
                        val atomicNumber =
                            backStackEntry.arguments?.getString("atomicNumber")?.toIntOrNull()
                        val element = elements.find { it.atomicNumber == atomicNumber }
                        if (element != null) {
                            ElementDetailScreen(element)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChemicalElementsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }


    val filteredElements = remember(searchQuery.text) {
        elements.filter {
            it.name.contains(searchQuery.text, ignoreCase = true) ||
                    it.symbol.contains(searchQuery.text, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color.White)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(40.dp)),
            placeholder = { Text("Search...") },
            shape = RoundedCornerShape(40.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                if (searchQuery.text.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = TextFieldValue("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Icon",
                            tint = Color.Gray
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Elements List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredElements) { element ->
                ElementCard(element) { selectedElement ->
                    navController.navigate("elementDetail/${selectedElement.atomicNumber}")
                }
            }
        }
    }
}

@Composable
fun ElementCard(element: Element, onClick: (Element) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick(element) } // Add clickable modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Element Symbol Box (Left Side - Square and White)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                    .background(color = Color.White, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = element.atomicNumber.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp),
                    color = Color.Gray
                )
                Text(
                    text = element.symbol,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            // Element Details (Right Side)
            Column {
                Text(
                    text = element.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = element.kind,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ElementDetailScreen(element: Element) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(128.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
                    .background(color = Color.White, shape = RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = element.atomicNumber.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    color = Color.Gray
                )

                Text(
                    text = element.symbol,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 45.sp
                )
            }
            Spacer(modifier = Modifier.height(48.dp))

            // Element Name
            Text(
                text = element.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 35.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Electron Configuration
            Text(
                text = electronConfigurations[element.atomicNumber - 1],
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Details List
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                DetailRow(label = "Kind", value = element.kind)
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(label = "Group", value = element.group?.toString() ?: "N/A")
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(label = "Period", value = element.period?.toString() ?: "N/A")
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(label = "Protons", value = element.atomicNumber.toString())
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(
                    label = "Neutrons",
                    value = (element.atomicMass?.toInt()?.minus(element.atomicNumber))?.toString()
                        ?: "N/A"
                )
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(label = "Electrons", value = element.atomicNumber.toString())
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(
                    label = "Electronegativity",
                    value = element.electronegativity?.toString() ?: "N/A"
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            fontFamily = FontFamily.SansSerif,
            fontSize = 18.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            fontFamily = FontFamily.SansSerif,
            fontSize = 18.sp
        )
    }
}


val elements = listOf(
    Element(
            "Hydrogen",
            "H",
            1,
            1.008,
            kind = "Nonmetal",
            state = "Gas",
            1,
            1,
            2.2f,
        ),
Element(
            "Helium",
            "He",
            2,
            4.002,
            kind = "Noble Gas",
            state = "Gas",
            1,
            18,
            null
        ),
Element(
            "Lithium",
            "Li",
            3,
            6.941,
            kind = "Alkali Metal",
            state = "Solid",
            2,
            1,
            0.98f,
        ),
Element(
            "Beryllium",
            "Be",
            4,
            9.012,
            kind = "Alkaline Earth Metal",
            state = "Solid",
            2,
            2,
            1.57f,
        ),
Element(
            "Boron",
            "B",
            5,
            10.811,
            kind = "Metalloid",
            state = "Solid",
            2,
            13,
            2.04f,
        ),
Element(
            "Carbon",
            "C",
            6,
            12.011,
            kind = "Nonmetal",
            state = "Solid",
            2,
            14,
            2.55f,
        ),
Element(
            "Nitrogen",
            "N",
            7,
            14.007,
            kind = "Nonmetal",
            state = "Gas",
            2,
            15,
            3.04f,
        ),
Element(
            "Oxygen",
            "O",
            8,
            15.999,
            kind = "Nonmetal",
            state = "Gas",
            2,
            16,
            3.44f,
        ),
Element(
            "Fluorine",
            "F",
            9,
            18.998,
            kind = "Halogen",
            state = "Gas",
            2,
            17,
            3.98f,
        ),
Element(
            "Neon",
            "Ne",
            10,
            20.18,
            kind = "Noble Gas",
            state = "Gas",
            2,
            18,
            null
        ),
Element(
            "Sodium",
            "Na",
            11,
            22.99,
            kind = "Alkali Metal",
            state = "Solid",
            3,
            1,
            0.93f,
        ),
Element(
            "Magnesium",
            "Mg",
            12,
            24.305,
            kind = "Alkaline Earth Metal",
            state = "Solid",
            3,
            2,
            1.31f,
        ),
Element(
            "Aluminum",
            "Al",
            13,
            26.982,
            kind = "Post Transition Metal",
            state = "Solid",
            3,
            13,
            1.61f,
        ),
Element(
            "Silicon",
            "Si",
            14,
            28.086,
            kind = "Metalloid",
            state = "Solid",
            3,
            14,
            1.9f,
        ),
Element(
            "Phosphorus",
            "P",
            15,
            30.974,
            kind = "Nonmetal",
            state = "Solid",
            3,
            15,
            2.19f,
        ),
Element(
            "Sulfur",
            "S",
            16,
            32.065,
            kind = "Nonmetal",
            state = "Solid",
            3,
            16,
            2.58f,
        ),
Element(
            "Chlorine",
            "Cl",
            17,
            35.453,
            kind = "Halogen",
            state = "Gas",
            1,
            17,
            3.16f,
        ),
Element(
            "Argon",
            "Ar",
            18,
            39.948,
            kind = "Noble Gas",
            state = "Gas",
            3,
            18,
            null
        ),
Element(
            "Potassium",
            "K",
            19,
            39.098,
            kind = "Alkali Metal",
            state = "Solid",
            4,
            1,
            0.82f,
        ),
Element(
            "Calcium",
            "Ca",
            20,
            40.078,
            kind = "Alkaline Earth Metal",
            state = "Solid",
            4,
            2,
            1f,
        ),
Element(
            "Scandium",
            "Sc",
            21,
            44.956,
            kind = "Transition Metal",
            state = "Solid",
            4,
            3,
            1.36f,
        ),
Element(
            "Titanium",
            "Ti",
            22,
            47.867,
            kind = "Transition Metal",
            state = "Solid",
            4,
            4,
            1.54f,
        ),
Element(
            "Vanadium",
            "V",
            23,
            50.942,
            kind = "Transition Metal",
            state = "Solid",
            4,
            5,
            1.63f,
        ),
Element(
            "Chromium",
            "Cr",
            24,
            51.996,
            kind = "Transition Metal",
            state = "Solid",
            4,
            6,
            1.66f,
        ),
Element(
            "Manganese",
            "Mn",
            25,
            54.938,
            kind = "Transition Metal",
            state = "Solid",
            4,
            7,
            1.55f,
        ),
Element(
            "Iron",
            "Fe",
            26,
            55.845,
            kind = "Transition Metal",
            state = "Solid",
            4,
            8,
            1.83f,
        ),
Element(
            "Cobalt",
            "Co",
            27,
            58.933,
            kind = "Transition Metal",
            state = "Solid",
            4,
            9,
            1.88f,
        ),
Element(
            "Nickel",
            "Ni",
            28,
            58.693,
            kind = "Transition Metal",
            state = "Solid",
            4,
            10,
            1.91f,
        ),
Element(
            "Copper",
            "Cu",
            29,
            63.546,
            kind = "Transition Metal",
            state = "Solid",
            4,
            11,
            1.9f,
        ),
Element(
            "Zinc",
            "Zn",
            30,
            65.38,
            kind = "Transition Metal",
            state = "Solid",
            4,
            12,
            1.65f,
        ),
Element(
            "Gallium",
            "Ga",
            31,
            69.723,
            kind = "Post Transition Metal",
            state = "Solid",
            4,
            13,
            1.81f,
        ),
Element(
            "Germanium",
            "Ge",
            32,
            72.64,
            kind = "Metalloid",
            state = "Solid",
            4,
            14,
            2.01f,
        ),
Element(
            "Arsenic",
            "As",
            33,
            74.922,
            kind = "Metalloid",
            state = "Solid",
            4,
            15,
            2.18f,
        ),
Element(
            "Selenium",
            "Se",
            34,
            78.96,
            kind = "Nonmetal",
            state = "Solid",
            4,
            16,
            2.55f,
        ),
Element(
            "Bromine",
            "Br",
            35,
            79.904,
            kind = "Halogen",
            state = "Liquid",
            4,
            17,
            2.96f,
        ),
Element(
            "Krypton",
            "Kr",
            36,
            83.798,
            kind = "Noble Gas",
            state = "Gas",
            4,
            18,
            null
        ),
Element(
            "Rubidium",
            "Rb",
            37,
            85.468,
            kind = "Alkali Metal",
            state = "Solid",
            5,
            1,
            0.82f,
        ),
Element(
            "Strontium",
            "Sr",
            38,
            87.62,
            kind = "Alkaline Earth Metal",
            state = "Solid",
            5,
            2,
            0.95f,
        ),
Element(
            "Yttrium",
            "Y",
            39,
            88.906,
            kind = "Transition Metal",
            state = "Solid",
            5,
            3,
            1.22f,
        ),
Element(
            "Zirconium",
            "Zr",
            40,
            91.224,
            kind = "Transition Metal",
            state = "Solid",
            5,
            4,
            1.33f,
        ),
Element(
            "Niobium",
            "Nb",
            41,
            98.906,
            kind = "Transition Metal",
            state = "Solid",
            5,
            5,
            1.6f,
        ),
Element(
            "Molybdenum",
            "Mo",
            42,
            95.96,
            kind = "Transition Metal",
            state = "Solid",
            5,
            6,
            2.16f,
        ),
Element(
            "Technetium",
            "Tc",
            43,
            98.0,
            kind = "Transition Metal",
            state = "Solid",
            5,
            7,
            1.9f,
        ),
Element(
            "Ruthenium",
            "Ru",
            44,
            101.107,
            kind = "Transition Metal",
            state = "Solid",
            5,
            8,
            2.2f,
        ),
Element(
            "Rhodium",
            "Rh",
            45,
            102.906,
            kind = "Transition Metal",
            state = "Solid",
            5,
            9,
            2.28f,
        ),
Element(
            "Palladium",
            "Pd",
            46,
            106.42,
            kind = "Transition Metal",
            state = "Solid",
            5,
            10,
            2.2f,
        ),
Element(
            "Silver",
            "Ag",
            47,
            07.869,
            kind = "Transition Metal",
            state = "Solid",
            5,
            11,
            1.93f,
        ),
Element(
            "Cadmium",
            "Cd",
            48,
            112.411,
            kind = "Transition Metal",
            state = "Solid",
            5,
            12,
            1.69f,
        ),
Element(
            "Indium",
            "In",
            49,
            114.818,
            kind = "Post Transition Metal",
            state = "Solid",
            5,
            13,
            1.78f,
        ),
Element(
            "Tin",
            "Sn",
            50,
            118.71,
            kind = "Post Transition Metal",
            state = "Solid",
            1,
            14,
            1.96f,
        ),
Element(
            "Antimony",
            "Sb",
            51,
            121.76,
            kind = "Metalloid",
            state = "Solid",
            5,
            15,
            2.05f,
        ),
Element(
            "Tellurium",
            "Te",
            52,
            127.6,
            kind = "Metalloid",
            state = "Solid",
            5,
            16,
            2.1f,
        ),
Element(
            "Iodine",
            "I",
            53,
            126.904,
            kind = "Halogen",
            state = "Solid",
            5,
            17,
            2.66f,
        ),
Element(
            "Xenon",
            "Xe",
            54,
            131.293,
            kind = "Noble Gas",
            state = "Gas",
            5,
            18,
            null
        ),
Element(
            "Caesium",
            "Cs",
            55,
            132.905,
            kind = "Alkali Metal",
            state = "Solid",
            6,
            1,
            0.79f,
        ),
Element(
            "Barium",
            "Ba",
            56,
            137.327,
            kind = "Alkaline Earth Metal",
            state = "Solid",
            6,
            2,
            0.89f,
        ),
Element(
            "Lanthanum",
            "La",
            57,
            138.905,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.1f,
        ),
Element(
            "Cerium",
            "Ce",
            58,
            140.116,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.12f,
        ),
Element(
            "Praseodymium",
            "Pr",
            59,
            140.908,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.13f,
        ),
Element(
            "Neodymium",
            "Nd",
            60,
            144.242,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.14f,
        ),
Element(
            "Promethium",
            "Pm",
            61,
            145.0,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.13f,
        ),
Element(
            "Samarium",
            "Sm",
            62,
            150.36,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.17f,
        ),
Element(
            "Europium",
            "Eu",
            63,
            151.964,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.2f,
        ),
Element(
            "Gadolinium",
            "Gd",
            64,
            157.25,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.2f,
        ),
Element(
            "Terbium",
            "Tb",
            65,
            158.925,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.2f,
        ),
Element(
            "Dysprosium",
            "Dy",
            66,
            162.5,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.22f,
        ),
Element(
            "Holmium",
            "Ho",
            67,
            164.93,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.23f,
        ),
Element(
            "Erbium",
            "Er",
            68,
            167.259,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.24f,
        ),
Element(
            "Thulium",
            "Tm",
            69,
            168.934,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.25f,
        ),
Element(
            "Ytterbium",
            "Yb",
            70,
            173.054,
            kind = "Lanthanide",
            state = "Solid",
            null,
            null,
            1.1f,
        ),
Element(
            "Lutetium",
            "Lu",
            71,
            174.967,
            kind = "Lanthanide",
            state = "Solid",
            6,
            18,
            1.27f,
        ),
Element(
            "Hafnium",
            "Hf",
            72,
            178.49,
            kind = "Transition Metal",
            state = "Solid",
            6,
            4,
            1.3f,
        ),
Element(
            "Tantalum",
            "Ta",
            73,
            180.948,
            kind = "Transition Metal",
            state = "Solid",
            6,
            5,
            1.5f,
        ),
Element(
            "Tungsten",
            "W",
            74,
            183.84,
            kind = "Transition Metal",
            state = "Solid",
            6,
            6,
            2.36f,
        ),
Element(
            "Rhenium",
            "Re",
            75,
            186.207,
            kind = "Transition Metal",
            state = "Solid",
            6,
            7,
            1.9f,
        ),
Element(
            "Osmium",
            "Os",
            76,
            190.23,
            kind = "Transition Metal",
            state = "Solid",
            6,
            8,
            2.2f,
        ),
Element(
            "Iridium",
            "Ir",
            77,
            192.217,
            kind = "Transition Metal",
            state = "Solid",
            6,
            9,
            2.2f,
        ),
Element(
            "Platinum",
            "Pt",
            78,
            195.084,
            kind = "Transition Metal",
            state = "Solid",
            6,
            10,
            2.28f,
        ),
Element(
            "Gold",
            "Au",
            79,
            196.967,
            kind = "Transition Metal",
            state = "Solid",
            6,
            11,
            2.54f,
        ),
Element(
            "Mercury",
            "Hg",
            80,
            200.59,
            kind = "Transition Metal",
            state = "Liquid",
            6,
            12,
            2f,
        ),
Element(
            "Thallium",
            "Tl",
            81,
            204.383,
            kind = "Actinide",
            state = "Solid",
            6,
            13,
            2.04f,
        ),
Element(
            "Lead",
            "Pb",
            82,
            207.2,
            kind = "Actinide",
            state = "Solid",
            6,
            14,
            2.33f,
        ),
Element(
            "Bismuth",
            "Bi",
            83,
            208.98,
            kind = "Actinide",
            state = "Solid",
            6,
            15,
            2.02f,
        ),
Element(
            "Polonium",
            "Po",
            84,
            210.0,
            kind = "Actinide",
            state = "Solid",
            6,
            16,
            2f,
        ),
Element(
            "Astatine",
            "At",
            85,
            210.0,
            kind = "Halogen",
            state = "Solid",
            6,
            17,
            2.2f,
        ),
Element(
            "Radon",
            "Rn",
            86,
            222.0,
            kind = "Noble Gas",
            state = "Gas",
            6,
            18,
            null
        ),
Element(
            "Francium",
            "Fr",
            87,
            223.0,
            kind = "Alkali Metal",
            state = "Solid",
            7,
            1,
            0.7f,
        ),
Element(
            "Radium",
            "Ra",
            88,
            226.0,
            kind = "Alkaline Earth Metal",
            state = "Solid",
            7,
            2,
            0.9f,
        ),
Element(
            "Actinium",
            "Ac",
            89,
            227.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.1f,
        ),
Element(
            "Thorium",
            "Th",
            90,
            232.038,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.3f,
        ),
Element(
            "Protactinium",
            "Pa",
            91,
            231.036,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.5f,
        ),
Element(
            "Uranium",
            "U",
            92,
            238.029,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.38f,
        ),
Element(
            "Neptunium",
            "Np",
            93,
            237.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.36f,
        ),
Element(
            "Plutonium",
            "Pu",
            94,
            244.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.28f,
        ),
Element(
            "Americium",
            "Am",
            95,
            243.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.3f,
        ),
Element(
            "Curium",
            "Cm",
            96,
            247.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.3f,
        ),
Element(
            "Berkelium",
            "Bk",
            97,
            247.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.3f,
        ),
Element(
            "Californium",
            "Cf",
            98,
            251.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.3f,
        ),
Element(
            "Einsteinium",
            "Es",
            99,
            252.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.3f,
        ),
Element(
            "Fermium",
            "Fm",
            100,
            257.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.3f,
        ),
Element(
            "Mendelevium",
            "Md",
            101,
            258.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.3f,
        ),
Element(
            "Nobelium",
            "No",
            102,
            259.0,
            kind = "Actinide",
            state = "Solid",
            null,
            null,
            1.3f,
        ),
Element(
            "Lawrencium",
            "Lr",
            103,
            262.0,
            kind = "Actinide",
            state = "Solid",
            7,
            18,
            null
        ),
Element(
            "Rutherfordium",
            "Rf",
            104,
            261.0,
            kind = "Transition Metal",
            state = "Solid",
            7,
            4,
            null
        ),
Element(
            "Dubnium",
            "Db",
            105,
            262.0,
            kind = "Transition Metal",
            state = "Solid",
            7,
            5,
            null
        ),
Element(
            "Seaborgium",
            "Sg",
            106,
            266.0,
            kind = "Transition Metal",
            state = "Solid",
            7,
            6,
            null
        ),
Element(
            "Bohrium",
            "Bh",
            107,
            264.0,
            kind = "Transition Metal",
            state = "Solid",
            7,
            7,
            null
        ),
Element(
            "Hassium",
            "Hs",
            108,
            267.0,
            kind = "Transition Metal",
            state = "Solid",
            7,
            8,
            null
        ),
Element(
            "Meitnerium",
            "Mt",
            109,
            268.0,
            kind = "Transition Metal",
            state = "Solid",
            7,
            9,
            null
        ),
Element(
            "Darmstadtium",
            "Ds",
            110,
            271.0,
            kind = "Transition Metal",
            state = "Solid",
            7,
            10,
            null
        ),
Element(
            "Roentgenium",
            "Rg",
            111,
            272.0,
            kind = "Transition Metal",
            state = "Solid",
            7,
            11,
            null
        ),
Element(
            "Copernicium",
            "Cn",
            112,
            285.0,
            kind = "Transition Metal",
            state = "Solid",
            7,
            12,
            null
        ),
Element(
            "Nihonium",
            "Nh",
            113,
            284.0,
            kind = "Post Transition Metal",
            state = "Solid",
            7,
            13,
            null
        ),
Element(
            "Flerovium",
            "Fl",
            114,
            289.0,
            kind = "Post Transition Metal",
            state = "Solid",
            7,
            14,
            null
        ),
Element(
            "Moscovium",
            "Mc",
            115,
            288.0,
            kind = "Post Transition Metal",
            state = "Solid",
            7,
            15,
            null
        ),
Element(
            "Livermorium",
            "Lv",
            116,
            292.0,
            kind = "Post Transition Metal",
            state = "Solid",
            7,
            16,
            null
        ),
Element(
            "Tennessine",
            "Ts",
            117,
            295.0,
            kind = "Halogen",
            state = "Solid",
            7,
            17,
            null
        ),
Element(
            "Oganesson",
            "Og",
            118,
            294.0,
            kind = "Noble Gas",
            state = "Gas",
            7,
            18,
            null
        )
)

val electronConfigurations = listOf(
            "1s1",
            "1s2",
            "1s2 2s1",
            "1s2 2s2",
            "1s2 2s2 2p1",
            "1s2 2s2 2p2",
            "1s2 2s2 2p3",
            "1s2 2s2 2p4",
            "1s2 2s2 2p5",
            "1s2 2s2 2p6",
            "1s2 2s2 2p6 3s1",
            "1s2 2s2 2p6 3s2",
            "1s2 2s2 2p6 3s2 3p1",
            "1s2 2s2 2p6 3s2 3p2",
            "1s2 2s2 2p6 3s2 3p3",
            "1s2 2s2 2p6 3s2 3p4",
            "1s2 2s2 2p6 3s2 3p5",
            "1s2 2s2 2p6 3s2 3p6",
            "1s2 2s2 2p6 3s2 3p6 4s1",
            "1s2 2s2 2p6 3s2 3p6 4s2",
            "1s2 2s2 2p6 3s2 3p6 3d1 4s2",
            "1s2 2s2 2p6 3s2 3p6 3d2 4s2",
            "1s2 2s2 2p6 3s2 3p6 3d3 4s2",
            "1s2 2s2 2p6 3s2 3p6 3d5 4s1",
            "1s2 2s2 2p6 3s2 3p6 3d5 4s2",
            "1s2 2s2 2p6 3s2 3p6 3d6 4s2",
            "1s2 2s2 2p6 3s2 3p6 3d7 4s2",
            "1s2 2s2 2p6 3s2 3p6 3d8 4s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s1",
            "1s2 2s2 2p6 3s2 3p6 3d1 4s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p3",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p4",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p5",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 5s1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 5s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d1 5s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d2 5s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d4 5s1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d5 5s1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d5 5s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d7 5s1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d8 5s1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p3",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p4",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p5",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p6",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p6 6s1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 5s2 5p6 5d1 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f1 5s2 5p6 5d1 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f3 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f4 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f5 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f6 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f7 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f7 5s2 5p6 5d1 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f9 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f10 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f11 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f12 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f13 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d1 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d2 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d3 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d4 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d5 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d6 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d7 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d9 6s1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p3",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p4",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p5",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p6",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s24p6 4d10 4f14 5s2 5p6 5d10 6s2 6p6 7s1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p6 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p6 6d1 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 6s2 6p6 6d2 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f2 6s2 6p6 6d1 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f3 6s2 6p6 6d1 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f4 6s2 6p6 6d1 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f6 6s2 6p6 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f7 6s2 6p6 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f7 6s2 6p6 6d1 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f9 6s2 6p6 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f10 6s2 6p6 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f11 6s2 6p6 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f12 6s2 6p6 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f13 6s2 6p6 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 7s2 7p1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d2 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d3 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d4 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d5 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d6 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d7 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d8 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d9 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p1",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p2",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p3",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p4",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p5",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p6",
            "1s2 2s2 2p6 3s2 3p6 3d10 4s2 4p6 4d10 4f14 5s2 5p6 5d10 5f14 6s2 6p6 6d10 7s2 7p6 8s1"
)
