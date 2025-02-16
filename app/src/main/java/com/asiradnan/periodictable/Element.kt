package com.asiradnan.periodictable

data class Element(
    val name: String,
    val symbol: String,
    val atomicNumber: Int,
    val atomicMass: Double?,
    val kind: String,
    val state: String,
    val period: Int?,
    val group: Int?,
    val electronegativity: Float?,
//    val electronConfiguration: String
)



