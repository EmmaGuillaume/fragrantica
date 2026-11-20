package com.example.fragrantica.ui.screens

import com.example.fragrantica.R
import com.example.fragrantica.data.PerfumeSummaryDto
import com.example.fragrantica.data.SearchPerfumeRpcDto
import com.example.fragrantica.ui.components.Perfume

fun resolvePerfumeDrawable(name: String): Int? {
    val n = name.lowercase()
    return when {
        "philosykos" in n -> R.drawable.img_philosykos
        "jardin" in n && "méditerranée" in n || "mediterranee" in n -> R.drawable.img_jardin_mediterranee
        "terre" in n && ("hermès" in n || "hermes" in n) -> R.drawable.img_terre_hermes
        "santal" in n -> R.drawable.img_santal_33
        "hiris" in n -> R.drawable.img_hiris
        "mandarine" in n && "basilic" in n -> R.drawable.img_mandarine_basilic
        else -> null
    }
}

fun formatGenderOrConcentration(raw: String?): String? = when (raw?.lowercase()) {
    "women", "femme" -> "Eau de toilette"
    "men", "homme" -> "Eau de toilette"
    "unisex", "mixte" -> "Eau de toilette"
    else -> raw
}

fun PerfumeSummaryDto.toCardPerfume() = Perfume(
    brand = brands.name,
    name = name,
    year = releaseYear,
    concentration = formatGenderOrConcentration(gender),
    rating = ratingValue ?: 0.0,
    reviewCount = ratingCount ?: 0,
    accords = accords.sortedBy { it.rank }.map { it.accords.name }.take(3),
    imageRes = resolvePerfumeDrawable(name),
)

fun SearchPerfumeRpcDto.toCardPerfume() = Perfume(
    brand = brandName,
    name = name,
    year = releaseYear,
    concentration = formatGenderOrConcentration(gender),
    rating = ratingValue ?: 0.0,
    reviewCount = ratingCount ?: 0,
    accords = accords.take(3),
    imageRes = resolvePerfumeDrawable(name),
)