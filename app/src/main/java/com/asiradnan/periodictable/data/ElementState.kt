package com.asiradnan.periodictable.data

enum class ElementState(val displayName: String) {
    GAS("Gas"),
    LIQUID("Liquid"),
    SOLID("Solid"),
    UNKNOWN("Unknown") // For synthetic elements that might not have a confirmed state
}