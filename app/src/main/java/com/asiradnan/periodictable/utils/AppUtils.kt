package com.asiradnan.periodictable.utils

import android.content.Context
import com.asiradnan.periodictable.data.ElementState
import com.asiradnan.periodictable.data.banglaKinds

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

object NumberTranslator {
    private val englishToBanglaMap = mapOf(
        '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
        '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯'
    )

    fun translateToBangla(number: String): String {
        return number.map { char ->
            englishToBanglaMap[char] ?: char
        }.joinToString("")
    }
}

// Helper functions moved here
fun toBanglaLabel(label: String): String {
    return when (label) {
        "Kind" -> "প্রকার"
        "Atomic Mass" -> "পারমাণবিক ভর"
        "Group" -> "গ্রুপ"
        "Period" -> "পর্যায়"
        "Protons" -> "প্রোটন"
        "Electrons" -> "ইলেকট্রন"
        "State" -> "অবস্থা"
        "Electronegativity" -> "তড়িৎঋণাত্মকতা"
        "Neutrons" -> "নিউট্রন"
        else -> label
    }
}

fun toBanglaState(state: ElementState): String {
    return when (state) {
        ElementState.GAS -> "বায়বীয়"
        ElementState.SOLID -> "কঠিন"
        ElementState.LIQUID -> "তরল"
        ElementState.UNKNOWN -> "অজ্ঞাত"
    }
}

fun toBanglaKind(kind: String): String {
    // Helper to safely get kind or return original English if missing
    fun safeGet(index: Int, fallback: String): String {
        return if (index in banglaKinds.indices) banglaKinds[index] else fallback
    }

    return when (kind) {
        "Nonmetal" -> safeGet(6, "Nonmetal")
        "Noble Gas" -> safeGet(8, "Noble Gas")
        "Alkali Metal" -> safeGet(0, "Alkali Metal")
        "Halogen" -> safeGet(7, "Halogen")
        "Alkaline Earth Metal" -> safeGet(1, "Alkaline Earth Metal")
        "Metalloid" -> safeGet(5, "Metalloid")
        "Lanthanide" -> safeGet(2, "Lanthanide")
        "Actinide" -> safeGet(3, "Actinide")
        "Transition Metal" -> safeGet(4, "Transition Metal")
        else -> "Post Transition Metal"
    }
}