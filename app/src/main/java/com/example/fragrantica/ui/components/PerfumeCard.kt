package com.example.fragrantica.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fragrantica.R
import com.fragrantica.ui.theme.FragranticaTheme
import com.fragrantica.ui.theme.Primaire
import com.fragrantica.ui.theme.Profond
import com.fragrantica.ui.theme.Secondaire
import java.util.Locale

data class Perfume(
    val brand: String,
    val name: String,
    val year: Int?,
    val concentration: String?,
    val rating: Double,
    val reviewCount: Int,
    val accords: List<String>,
    val imageUrl: String? = null,
    @param:DrawableRes val imageRes: Int? = null,
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
    if (perfume.imageRes != null) {
        Image(
            painter = painterResource(id = perfume.imageRes),
            contentDescription = perfume.name,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
    } else {
        // Fallback quand aucune image locale n'est fournie
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
    }
}

@Composable
private fun PerfumeBrand(
    brand: String,
    color: Color = Secondaire,
) {
    Text(
        text = brand.uppercase(Locale.FRENCH),
        style = MaterialTheme.typography.labelMedium,
        color = color,
        letterSpacing = 0.6.sp,
    )
}

@Composable
private fun PerfumeName(
    name: String,
    fontSize: TextUnit = 17.sp,
    lineHeight: TextUnit = 22.sp,
) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleLarge.copy(
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontWeight = FontWeight.SemiBold,
        ),
        color = Profond,
    )
}

@Composable
private fun PerfumeRating(
    rating: Double,
    fontSize: TextUnit = 15.sp,
) {
    Text(
        text = "★ ${String.format(Locale.US, "%.2f", rating).replace('.', ',')} / 5",
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
        ),
        color = Profond,
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
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        PerfumeImage(
            perfume = perfume,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(167f / 209f),
        )
        PerfumeBrand(
            brand = perfume.brand,
            color = Secondaire,
        )
        PerfumeName(
            name = perfume.name,
            fontSize = 17.sp,
            lineHeight = 22.sp,
        )

        val meta = listOfNotNull(perfume.year?.toString(), perfume.concentration)
            .joinToString(" · ")
        if (meta.isNotEmpty()) {
            Text(
                text = meta,
                style = MaterialTheme.typography.bodySmall,
                color = Secondaire,
            )
        }

        PerfumeRating(
            rating = perfume.rating,
            fontSize = 15.sp,
        )

        if (perfume.accords.isNotEmpty()) {
            Text(
                text = perfume.accords.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = Secondaire,
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
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PerfumeImage(
            perfume = perfume,
            modifier = Modifier.size(width = 96.dp, height = 120.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            PerfumeBrand(
                brand = perfume.brand,
                color = Primaire,
            )
            PerfumeName(
                name = perfume.name,
                fontSize = 20.sp,
                lineHeight = 24.sp,
            )
            PerfumeRating(
                rating = perfume.rating,
                fontSize = 16.sp,
            )
            val formattedReviews = String.format(Locale.US, "%,d", perfume.reviewCount)
                .replace(',', ' ')
            Text(
                text = "$formattedReviews avis",
                style = MaterialTheme.typography.bodySmall,
                color = Secondaire,
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
        reviewCount = 8624,
        accords = listOf("Vert", "Boisé"),
        imageRes = R.drawable.img_philosykos,
    )

    FragranticaTheme {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            PerfumeCard(
                philosykos,
                variant = PerfumeCardVariant.Vertical,
                modifier = Modifier.width(167.dp),
            )
            PerfumeCard(philosykos, variant = PerfumeCardVariant.Horizontal)
        }
    }
}