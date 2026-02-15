package com.asiradnan.periodictable

import com.asiradnan.periodictable.data.Element
import com.asiradnan.periodictable.data.ElementState
import com.asiradnan.periodictable.utils.filterElements
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilteringLogicTest {

    // Mock Data for Testing
    private val mockElements = listOf(
        createMockElement(1, "Hydrogen", "H", ElementState.GAS),
        createMockElement(8, "Oxygen", "O", ElementState.GAS),
        createMockElement(26, "Iron", "Fe", ElementState.SOLID),
        createMockElement(80, "Mercury", "Hg", ElementState.LIQUID)
    )
    private val mockBanglaNames = listOf("হাইড্রোজেন", "অক্সিজেন", "আয়রন", "পারদ")

    // Helper to create dummy elements
    private fun createMockElement(number: Int, name: String, symbol: String, state: ElementState) = Element(
        name = name, symbol = symbol, atomicNumber = number, atomicMass = 1.0,
        kind = "Test", state = state, period = 1, group = 1, electronegativity = 1.0, electronConfiguration = ""
    )

    // [Unit] "search for the elements by their atomic number" -> "1"
    @Test
    fun searchByAtomicNumber_returnsCorrectElement() {
        val result = filterElements(mockElements, mockBanglaNames, "1", null, true)

        // Should find Hydrogen (1)
        assertTrue(result.any { it.name == "Hydrogen" })
    }

    // [Unit] "search for the elements by their atomic number" -> "8" (Not hardcoded)
    @Test
    fun searchByAtomicNumber_returnsDifferentElement() {
        val result = filterElements(mockElements, mockBanglaNames, "8", null, true)

        // Should find Oxygen (8) and Mercury (80) because "80" contains "8"
        // But specifically, ensure Oxygen is there
        assertTrue(result.any { it.name == "Oxygen" })
    }

    // [Unit] "filter them by their state" -> Gas
    @Test
    fun filterByState_returnsOnlyGas() {
        val result = filterElements(mockElements, mockBanglaNames, "", ElementState.GAS, true)

        assertEquals(2, result.size) // Hydrogen + Oxygen
        assertTrue(result.all { it.state == ElementState.GAS })
    }

    // [Unit] "filter them by their state" -> Solid
    @Test
    fun filterByState_returnsOnlySolid() {
        val result = filterElements(mockElements, mockBanglaNames, "", ElementState.SOLID, true)

        assertEquals(1, result.size)
        assertEquals("Iron", result[0].name)
    }

    // [Unit] Combined: Search "8" AND Filter "Liquid"
    @Test
    fun searchAndFilter_combined() {
        // Search "8" matches Oxygen (8, Gas) and Mercury (80, Liquid)
        // Filter "Liquid" should leave only Mercury
        val result = filterElements(mockElements, mockBanglaNames, "8", ElementState.LIQUID, true)

        assertEquals(1, result.size)
        assertEquals("Mercury", result[0].name)
    }

    // [Unit] Edge Case: Non-numeric search text
    @Test
    fun searchNonNumeric_noMatch() {
        val result = filterElements(mockElements, mockBanglaNames, "abc", null, true)
        assertEquals(0, result.size)
    }
}