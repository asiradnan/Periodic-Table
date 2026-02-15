package com.asiradnan.periodictable.utils

import com.asiradnan.periodictable.data.Element
import com.asiradnan.periodictable.data.ElementState

fun filterElements(
    allElements: List<Element>,
    banglaNamesList: List<String>, // Pass this in so we can test with fake data
    query: String,
    stateFilter: ElementState?,
    isEnglish: Boolean
): List<Element> {

    val textFiltered = if (isEnglish) {
        allElements.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.symbol.contains(query, ignoreCase = true) ||
                    it.atomicNumber.toString().contains(query)
        }
    } else {
        allElements.filterIndexed { index, element ->
            // Safe access to bangla name
            val bName = banglaNamesList.getOrElse(index) { "" }

            val matchesBanglaName = bName.contains(query, ignoreCase = true)
            val matchesSymbol = element.symbol.contains(query, ignoreCase = true)
            val matchesNumber = element.atomicNumber.toString().contains(query) // Numeric search works in Bangla mode too

            matchesBanglaName || matchesSymbol || matchesNumber
        }
    }

    return if (stateFilter != null) {
        textFiltered.filter { it.state == stateFilter }
    } else {
        textFiltered
    }
}