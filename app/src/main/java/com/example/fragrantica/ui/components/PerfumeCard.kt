package com.example.fragrantica.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fragrantica.ui.theme.FragranticaTheme

data class Perfume(
    val brand: String,
    val name: String,
    val year: Int?,
    val concentration: String?,
    val rating: Double,
    val reviewCount: Int,
    val accords: List<String>,
    val imageUrl: String? = null,
)

enum class PerfumeCardVariant { Vertical, Horizontal }

@Composable
fun PerfumeCard(
    perfume: Perfume,
    modifier: Modifier = Modifier,
    variant: PerfumeCardVariant = PerfumeCardVariant.Vertical,
    onClick: () -> Unit = {},
) {
    when (variant) {
        PerfumeCardVariant.Vertical -> VerticalPerfumeCard(perfume, modifier, onClick)
        PerfumeCardVariant.Horizontal -> HorizontalPerfumeCard(perfume, modifier, onClick)
    }
}

@Composable
private fun PerfumeImage(perfume: Perfume, modifier: Modifier = Modifier) {
    // À remplacer par AsyncImage (Coil) quand tu auras de vraies images
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

@Composable
private fun PerfumeBrand(brand: String) {
    Text(
        text = brand.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun PerfumeName(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun PerfumeRating(rating: Double) {
    Text(
        text = "★ ${"%.2f".format(rating).replace('.', ',')} / 5",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun VerticalPerfumeCard(
    perfume: Perfume,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PerfumeImage(
            perfume,
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        )
        PerfumeBrand(perfume.brand)
        PerfumeName(perfume.name)

        val meta = listOfNotNull(perfume.year?.toString(), perfume.concentration)
            .joinToString(" · ")
        if (meta.isNotEmpty()) {
            Text(
                meta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        PerfumeRating(perfume.rating)

        if (perfume.accords.isNotEmpty()) {
            Text(
                perfume.accords.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HorizontalPerfumeCard(
    perfume: Perfume,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PerfumeImage(perfume, Modifier.size(96.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            PerfumeBrand(perfume.brand)
            PerfumeName(perfume.name)
            PerfumeRating(perfume.rating)
            Text(
                "${"%,d".format(perfume.reviewCount).replace(',', ' ')} avis",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfumeCardPreview() {
    val philosykos = Perfume(
        brand = "Diptyque",
        name = "Philosykos",
        year = 1996,
        concentration = "Eau de toilette",
        rating = 4.18,
        reviewCount = 8200,
        accords = listOf("Vert", "Boisé"),
    )

    FragranticaTheme {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            PerfumeCard(
                philosykos,
                variant = PerfumeCardVariant.Vertical,
                modifier = Modifier.width(180.dp),
            )
            PerfumeCard(philosykos, variant = PerfumeCardVariant.Horizontal)
        }
    }
}