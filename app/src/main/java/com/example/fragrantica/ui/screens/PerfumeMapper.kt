package com.example.fragrantica.ui.screens

import com.example.fragrantica.data.PerfumeSummaryDto
import com.example.fragrantica.ui.components.Perfume

fun PerfumeSummaryDto.toCardPerfume() = Perfume(
    brand = brands.name,
    name = name,
    year = releaseYear,
    concentration = gender,
    rating = ratingValue ?: 0.0,
    reviewCount = ratingCount ?: 0,
    accords = accords.sortedBy { it.rank }.map { it.accords.name }.take(3)
)