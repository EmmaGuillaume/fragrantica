package com.example.fragrantica.data
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PerfumeDto(
    val id: Int,
    @SerialName("fragrantica_id") val fragranticaId: Int? = null,
    val name: String,
    val url: String,
    val gender: String? = null,
    @SerialName("release_year") val releaseYear: Int? = null,
    @SerialName("rating_value") val ratingValue: Double? = null,
    @SerialName("rating_count") val ratingCount: Int? = null,
    val brands: BrandDto,
    @SerialName("perfume_accords") val accords: List<PerfumeAccordDto> = emptyList(),
    @SerialName("perfume_notes") val notes: List<PerfumeNoteDto> = emptyList(),
    @SerialName("perfume_perfumers") val perfumers: List<PerfumePerfumerDto> = emptyList()
)

@Serializable data class BrandDto(val name: String, val countries: CountryDto? = null)
@Serializable data class CountryDto(val name: String)
@Serializable data class NameDto(val name: String)
@Serializable data class PerfumeAccordDto(val rank: Int, val accords: NameDto)
@Serializable data class PerfumeNoteDto(val layer: String, val notes: NameDto)
@Serializable data class PerfumePerfumerDto(val perfumers: NameDto)

@Serializable
data class PerfumeSummaryDto(
    val id: Int,
    val name: String,
    val gender: String? = null,
    @SerialName("release_year") val releaseYear: Int? = null,
    @SerialName("rating_value") val ratingValue: Double? = null,
    @SerialName("rating_count") val ratingCount: Int? = null,
    val brands: BrandDto,
    @SerialName("perfume_accords") val accords: List<PerfumeAccordDto> = emptyList()
)