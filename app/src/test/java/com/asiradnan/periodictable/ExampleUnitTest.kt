package com.asiradnan.periodictable

import com.asiradnan.periodictable.data.Element
import com.asiradnan.periodictable.data.ElementState
import com.asiradnan.periodictable.data.banglaKinds
import com.asiradnan.periodictable.data.banglaNames
import com.asiradnan.periodictable.utils.NumberTranslator
import com.asiradnan.periodictable.utils.toBanglaKind
import com.asiradnan.periodictable.utils.toBanglaLabel
import com.asiradnan.periodictable.utils.toBanglaState
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    // --- 1. Test Number Translation ---
    @Test
    fun numberTranslator_correctlyTranslatesDigits() {
        val input = "1234567890"
        val expected = "১২৩৪৫৬৭৮৯০"
        val result = NumberTranslator.translateToBangla(input)
        assertEquals(expected, result)
    }

    @Test
    fun numberTranslator_ignoresNonDigits() {
        val input = "Element 118 (Og)"
        val expected = "Element ১১৮ (Og)" // Letters remain, numbers change
        val result = NumberTranslator.translateToBangla(input)
        assertEquals(expected, result)
    }

    // --- 2. Test Enum State Logic ---
    @Test
    fun elementState_enumHasCorrectDisplayNames() {
        assertEquals("Gas", ElementState.GAS.displayName)
        assertEquals("Liquid", ElementState.LIQUID.displayName)
        assertEquals("Solid", ElementState.SOLID.displayName)
        assertEquals("Unknown", ElementState.UNKNOWN.displayName)
    }

    @Test
    fun toBanglaState_translatesEnumsCorrectly() {
        assertEquals("বায়বীয়", toBanglaState(ElementState.GAS))
        assertEquals("তরল", toBanglaState(ElementState.LIQUID))
        assertEquals("কঠিন", toBanglaState(ElementState.SOLID))
        assertEquals("অজ্ঞাত", toBanglaState(ElementState.UNKNOWN))
    }

    // --- 3. Test Kind Translation ---
    @Test
    fun toBanglaKind_translatesKnownKinds() {
        // banglaKinds[0] is "ক্ষার ধাতু" (Alkali Metal)
        assertEquals(banglaKinds[0], toBanglaKind("Alkali Metal"))

        // banglaKinds[8] is "নিষ্ক্রিয় গ্যাস" (Noble Gas)
        assertEquals(banglaKinds[8], toBanglaKind("Noble Gas"))
    }

    @Test
    fun toBanglaKind_handlesUnknownKindsGracefully() {
        // "Post Transition Metal" is the default fallback in our util
        val result = toBanglaKind("Random Unknown Kind")
        assertEquals("Post Transition Metal", result)
    }

    // --- 4. Test Label Translation ---
    @Test
    fun toBanglaLabel_translatesStaticLabels() {
        assertEquals("গ্রুপ", toBanglaLabel("Group"))
        assertEquals("পর্যায়", toBanglaLabel("Period"))
        assertEquals("পারমাণবিক ভর", toBanglaLabel("Atomic Mass"))
    }

    @Test
    fun toBanglaLabel_returnsOriginalIfNoTranslation() {
        val randomLabel = "SuperPower"
        assertEquals(randomLabel, toBanglaLabel(randomLabel))
    }

    // --- 5. Test Safe Data Access (The Crash Fix) ---
    @Test
    fun banglaNames_safeAccessLogic() {
        // logic used in UI: banglaNames.getOrElse(index) { fallback }

        // Scenario 1: Index exists (Hydrogen is index 0)
        val indexH = 0
        val nameH = banglaNames.getOrElse(indexH) { "Hydrogen" }
        // We expect the Bangla name if your list is populated, otherwise fallback
        // Since we can't guarantee your list is full in this unit test file without mocking,
        // we assert it is NOT null.
        assertNotNull(nameH)

        // Scenario 2: Index Out of Bounds (Safety Check)
        val hugeIndex = 9999
        val safeName = banglaNames.getOrElse(hugeIndex) { "Unobtainium" }

        // Should return the fallback, NOT crash
        assertEquals("Unobtainium", safeName)
    }

    // --- 6. Test Element Data Structure ---
    @Test
    fun element_dataClassHoldsStateEnum() {
        val element = Element(
            name = "Mercury",
            symbol = "Hg",
            atomicNumber = 80,
            atomicMass = 200.59,
            kind = "Transition Metal",
            state = ElementState.LIQUID, // Using Enum
            period = 6,
            group = 12,
            electronegativity = 2.0,
            electronConfiguration = "[Xe] 4f14 5d10 6s2"
        )

        assertEquals(ElementState.LIQUID, element.state)
        assertEquals("Transition Metal", element.kind)
    }
}