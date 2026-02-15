package com.asiradnan.periodictable.data

data class Element(
    val name: String,
    val symbol: String,
    val atomicNumber: Int,
    val atomicMass: Double,
    val kind: String,
    val state: ElementState, // Changed from String to ElementState
    val period: Int?,
    val group: Int?,
    val electronegativity: Double?,
    val electronConfiguration: String
)