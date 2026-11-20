package com.example.fragrantica.ui.screens

import com.example.fragrantica.R
import com.example.fragrantica.data.PerfumeSummaryDto
import com.example.fragrantica.data.SearchPerfumeRpcDto
import com.example.fragrantica.ui.components.Perfume
import kotlin.math.abs

private val fallbackBottleDrawables = listOf(
    R.drawable.img_philosykos,
    R.drawable.img_jardin_mediterranee,
    R.drawable.img_terre_hermes,
    R.drawable.img_santal_33,
    R.drawable.img_hiris,
    R.drawable.img_mandarine_basilic,
)

private val knownBrandFormatting = mapOf(
    "hermes" to "Hermès",
    "yves-saint-laurent" to "Yves Saint Laurent",
    "dolce-gabbana" to "Dolce & Gabbana",
    "lancome" to "Lancôme",
    "chloe" to "Chloé",
    "acqua-di-parma" to "Acqua di Parma",
    "jean-paul-gaultier" to "Jean Paul Gaultier",
    "le-labo" to "Le Labo",
    "tom-ford" to "Tom Ford",
    "giorgio-armani" to "Giorgio Armani",
    "calvin-klein" to "Calvin Klein",
    "paco-rabanne" to "Paco Rabanne",
    "narciso-rodriguez" to "Narciso Rodriguez",
    "viktor-rolf" to "Viktor & Rolf",
    "issey-miyake" to "Issey Miyake",
    "carolina-herrera" to "Carolina Herrera",
    "o-boticario" to "O Boticário",
    "bath-body-works" to "Bath & Body Works",
    "lattafa-perfumes" to "Lattafa Perfumes",
    "frederic-malle" to "Frédéric Malle",
    "maison-francis-kurkdjian" to "Maison Francis Kurkdjian",
    "etat-libre-d-orange" to "État Libre d’Orange",
    "l-artisan-parfumeur" to "L’Artisan Parfumeur",
    "annick-goutal" to "Annick Goutal",
    "serge-lutens" to "Serge Lutens",
    "van-cleef-arpels" to "Van Cleef & Arpels",
)

private val lowercaseParticles = setOf(
    "de", "du", "des", "en", "la", "le", "les", "a", "au", "aux", "et", "of", "the", "in", "for", "by", "di", "da",
)

private val apostrophePrefixes = setOf("d", "l", "j", "n", "s", "c", "m", "t")

private val accentedWordReplacements = mapOf(
    "hermes" to "Hermès",
    "mediterranee" to "Méditerranée",
    "epice" to "Épicé",
    "poudre" to "Poudré",
    "ete" to "Été",
    "cedre" to "Cèdre",
    "vetiver" to "Vétiver",
    "ambree" to "Ambrée",
    "ambre" to "Ambre",
    "fraiche" to "Fraîche",
    "givree" to "Givrée",
    "tres" to "Très",
)

fun formatDisplayName(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return trimmed
    // Si la chaîne contient déjà des majuscules, on la conserve telle quelle
    if (trimmed.any { it.isUpperCase() }) return trimmed

    knownBrandFormatting[trimmed.lowercase()]?.let { return it }

    val tokens = trimmed.split('-')
    if (tokens.isEmpty()) return trimmed

    val sb = StringBuilder()
    var i = 0
    while (i < tokens.size) {
        val token = tokens[i]
        if (token.isEmpty()) {
            i++
            continue
        }
        val nextToken = tokens.getOrNull(i + 1)
        if (token in apostrophePrefixes && nextToken != null && nextToken.isNotEmpty()) {
            if (sb.isNotEmpty()) sb.append(' ')
            val prefix = if (i == 0) token.uppercase() else token.lowercase()
            val formattedNext = accentedWordReplacements[nextToken]
                ?: nextToken.replaceFirstChar { it.titlecase() }
            sb.append(prefix).append('’').append(formattedNext)
            i += 2
            continue
        }

        if (sb.isNotEmpty()) sb.append(' ')
        val formatted = when {
            accentedWordReplacements.containsKey(token) -> accentedWordReplacements.getValue(token)
            i > 0 && token in lowercaseParticles -> if (token == "a") "à" else token
            else -> token.replaceFirstChar { it.titlecase() }
        }
        sb.append(formatted)
        i++
    }
    return sb.toString()
}

fun resolvePerfumeDrawable(name: String, id: Int = 0): Int? {
    val n = name.lowercase()
    return when {
        "philosykos" in n -> R.drawable.img_philosykos
        "jardin" in n && ("méditerranée" in n || "mediterranee" in n) -> R.drawable.img_jardin_mediterranee
        "terre" in n && ("hermès" in n || "hermes" in n) -> R.drawable.img_terre_hermes
        "santal" in n -> R.drawable.img_santal_33
        "hiris" in n -> R.drawable.img_hiris
        "mandarine" in n && "basilic" in n -> R.drawable.img_mandarine_basilic
        else -> {
            val seed = if (id != 0) id else n.hashCode()
            fallbackBottleDrawables[abs(seed) % fallbackBottleDrawables.size]
        }
    }
}

fun formatGenderOrConcentration(raw: String?, perfumeName: String? = null): String? {
    val n = perfumeName?.lowercase().orEmpty()
    val concentrationFromName = when {
        "eau-de-parfum" in n || "eau de parfum" in n -> "Eau de parfum"
        "eau-de-toilette" in n || "eau de toilette" in n -> "Eau de toilette"
        "eau-de-cologne" in n || "eau de cologne" in n -> "Eau de cologne"
        else -> null
    }
    val genderLabel = when (raw?.lowercase()) {
        "women", "femme" -> "Femme"
        "men", "homme" -> "Homme"
        "unisex", "mixte" -> "Mixte"
        else -> raw
    }
    return when {
        concentrationFromName != null && genderLabel != null && genderLabel != concentrationFromName ->
            "$genderLabel · $concentrationFromName"
        genderLabel != null -> genderLabel
        else -> concentrationFromName
    }
}

fun PerfumeSummaryDto.toCardPerfume() = Perfume(
    brand = formatDisplayName(brands.name),
    name = formatDisplayName(name),
    year = releaseYear,
    concentration = formatGenderOrConcentration(gender, name),
    rating = ratingValue ?: 0.0,
    reviewCount = ratingCount ?: 0,
    accords = accords.sortedBy { it.rank }.map { formatDisplayName(it.accords.name) }.take(3),
    imageRes = resolvePerfumeDrawable(name, id),
)

fun SearchPerfumeRpcDto.toCardPerfume() = Perfume(
    brand = formatDisplayName(brandName),
    name = formatDisplayName(name),
    year = releaseYear,
    concentration = formatGenderOrConcentration(gender, name),
    rating = ratingValue ?: 0.0,
    reviewCount = ratingCount ?: 0,
    accords = accords.take(3).map { formatDisplayName(it) },
    imageRes = resolvePerfumeDrawable(name, id),
)
