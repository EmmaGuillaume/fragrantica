package com.example.fragrantica.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fragrantica.R
import com.example.fragrantica.data.PerfumeSummaryDto
import com.example.fragrantica.navigation.Routes
import com.example.fragrantica.ui.components.PerfumeCard
import com.example.fragrantica.ui.components.PerfumeCardVariant
import com.fragrantica.ui.theme.Blanc
import com.fragrantica.ui.theme.Bois
import com.fragrantica.ui.theme.Chaux
import com.fragrantica.ui.theme.Ciel
import com.fragrantica.ui.theme.FragranticaTheme
import com.fragrantica.ui.theme.Mandarine
import com.fragrantica.ui.theme.Manrope
import com.fragrantica.ui.theme.Primaire
import com.fragrantica.ui.theme.Profond
import com.fragrantica.ui.theme.Rose
import com.fragrantica.ui.theme.Sable
import com.fragrantica.ui.theme.Secondaire
import com.fragrantica.ui.theme.Texte
import com.fragrantica.ui.theme.VertClair

private data class AccordItem(
    val name: String,
    @param:DrawableRes val imageRes: Int,
    val backgroundColor: Color,
)

private data class BrandItem(
    val name: String,
    val country: String,
)

private data class JournalArticle(
    val category: String,
    val title: String,
    val readTime: String,
    @param:DrawableRes val imageRes: Int,
)

private val accordsList = listOf(
    AccordItem("Agrumes", R.drawable.img_mandarine, Mandarine),
    AccordItem("Aquatique", R.drawable.img_journal_ete, Ciel),
    AccordItem("Boisé", R.drawable.img_atelier, Bois),
    AccordItem("Floral", R.drawable.img_figue, Rose),
    AccordItem("Gourmand", R.drawable.img_mandarine, Sable),
    AccordItem("Vert", R.drawable.img_figue, VertClair),
)

private val featuredBrands = listOf(
    BrandItem("Diptyque", "France"),
    BrandItem("Hermès", "France"),
    BrandItem("Le Labo", "États-Unis"),
    BrandItem("Acqua di Parma", "Italie"),
)

private val journalArticles = listOf(
    JournalArticle(
        category = "GUIDES",
        title = "Les parfums qui sentent l’été",
        readTime = "5 min de lecture",
        imageRes = R.drawable.img_journal_ete,
    ),
    JournalArticle(
        category = "MATIÈRES",
        title = "La mandarine, une lumière en flacon",
        readTime = "5 min de lecture",
        imageRes = R.drawable.img_mandarine,
    ),
    JournalArticle(
        category = "PARFUMEURS",
        title = "Dans l’atelier des émotions",
        readTime = "5 min de lecture",
        imageRes = R.drawable.img_atelier,
    ),
)

@Composable
fun AccueilScreen(
    navController: NavHostController? = null,
    modifier: Modifier = Modifier,
    vm: AccueilViewModel = viewModel(),
) {
    val state by vm.state.collectAsState()

    val navigateTo: (String) -> Unit = { route ->
        navController?.navigate(route) { launchSingleTop = true }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Chaux)
    ) {
        val isDesktop = maxWidth >= 840.dp
        val horizontalPadding = if (isDesktop) 80.dp else 20.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HeroSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onSearchSubmit = { q -> navigateTo(Routes.rechercheRoute(q = q)) },
                onTagClick = { tag -> navigateTo(Routes.rechercheRoute(note = tag)) },
            )

            NouveautesSection(
                state = state,
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onPerfumeClick = { id -> navigateTo(Routes.detailRoute(id)) },
                onExploreAllClick = { navigateTo(Routes.PARFUMS) },
                onRetry = vm::load,
            )

            ExplorerParAccordSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onAccordClick = { accord -> navigateTo(Routes.rechercheRoute(accord = accord.name)) },
            )

            MarquesALaUneSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onBrandClick = { brand -> navigateTo(Routes.rechercheRoute(brand = brand.name)) },
            )

            LeJournalSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onArticleClick = { navigateTo(Routes.ACTUALITES) },
            )

            CreezVotreCollectionSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onCreateAccountClick = { navigateTo(Routes.PARFUMS) },
            )

            FooterSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onNavigate = navigateTo,
            )
        }
    }
}

@Composable
private fun SectionEyebrow(
    text: String,
    color: Color = Primaire,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
        ),
        color = color,
        modifier = modifier,
    )
}

@Composable
private fun HeroSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onSearchSubmit: (String) -> Unit,
    onTagClick: (String) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isDesktop) 40.dp else 16.dp,
                bottom = if (isDesktop) 56.dp else 44.dp,
            )
    ) {
        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    SectionEyebrow(text = "LE PARFUM, AUTREMENT")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Trouvez le parfum\nqui vous ressemble",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 52.sp,
                            lineHeight = 60.sp,
                        ),
                        color = Profond,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Une note, un souvenir, une nouvelle histoire.\nExplorez l’univers du parfum, à votre rythme.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Secondaire,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    HeroSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = "Parfum, marque, note, parfumeur...",
                        onSearchClick = { onSearchSubmit(searchQuery) },
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "À découvrir",
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondaire,
                        )
                        listOf("Figue", "Sel marin", "Mandarine").forEach { tag ->
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = Primaire,
                                modifier = Modifier.clickable { onTagClick(tag) },
                            )
                        }
                    }
                }

                HeroCollage(
                    modifier = Modifier.weight(1f),
                    leftHeight = 460.dp,
                    rightTopHeight = 220.dp,
                    rightBottomHeight = 190.dp,
                    rightTopOffset = 36.dp,
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            SectionEyebrow(text = "UN MONDE DE PARFUMS. UNE COMMUNAUTÉ DE PASSIONNÉS.")
        } else {
            SectionEyebrow(text = "LE PARFUM, AUTREMENT")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Trouvez le parfum\nqui vous\nressemble",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 38.sp,
                    lineHeight = 46.sp,
                ),
                color = Profond,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Une note, un souvenir, une nouvelle histoire.\nExplorez l’univers du parfum, à votre rythme.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                ),
                color = Secondaire,
            )
            Spacer(modifier = Modifier.height(28.dp))
            HeroCollage(
                modifier = Modifier.fillMaxWidth(),
                leftHeight = 300.dp,
                rightTopHeight = 148.dp,
                rightBottomHeight = 144.dp,
                rightTopOffset = 24.dp,
            )
            Spacer(modifier = Modifier.height(28.dp))
            HeroSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Parfum, marque, note...",
                onSearchClick = { onSearchSubmit(searchQuery) },
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "UN MONDE DE PARFUMS. UNE COMMUNAUTÉ DE\nPASSIONNÉS.",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp,
                    letterSpacing = 0.6.sp,
                ),
                color = Primaire,
            )
        }
    }
}

@Composable
private fun HeroCollage(
    modifier: Modifier = Modifier,
    leftHeight: Dp,
    rightTopHeight: Dp,
    rightBottomHeight: Dp,
    rightTopOffset: Dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_hero_arch),
            contentDescription = "Arche méditerranéenne",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(0.625f)
                .height(leftHeight)
                .clip(RoundedCornerShape(20.dp)),
        )
        Column(
            modifier = Modifier
                .weight(0.375f)
                .padding(top = rightTopOffset),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_mandarine),
                contentDescription = "Mandarines au bord de l’eau",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rightTopHeight)
                    .clip(RoundedCornerShape(16.dp)),
            )
            Image(
                painter = painterResource(id = R.drawable.img_figue),
                contentDescription = "Feuilles de figuier",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rightBottomHeight)
                    .clip(RoundedCornerShape(16.dp)),
            )
        }
    }
}

@Composable
private fun HeroSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(50))
            .background(Blanc)
            .border(1.dp, Sable, RoundedCornerShape(50))
            .padding(start = 20.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Secondaire,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Texte),
                cursorBrush = SolidColor(Primaire),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Button(
            onClick = onSearchClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primaire,
                contentColor = Blanc,
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            modifier = Modifier.height(48.dp),
        ) {
            Text(
                text = "Rechercher",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
private fun NouveautesSection(
    state: AccueilState,
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onPerfumeClick: (Int) -> Unit,
    onExploreAllClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 24.dp,
                bottom = 48.dp,
            )
    ) {
        SectionEyebrow(text = "LES NOUVELLES FICHES DE LA BASE")
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Nouveautés",
                style = MaterialTheme.typography.headlineLarge,
                color = Profond,
            )
            if (isDesktop) {
                Text(
                    text = "Tout explorer →",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primaire,
                    modifier = Modifier.clickable(onClick = onExploreAllClick),
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        when (state) {
            AccueilState.Loading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is AccueilState.Error -> Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onRetry) { Text("Réessayer") }
            }

            is AccueilState.Success ->
                if (state.perfumes.isEmpty()) {
                    Text(
                        text = "Aucun parfum trouvé.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Secondaire,
                    )
                } else {
                    PerfumeGrid(
                        perfumes = state.perfumes,
                        columns = if (isDesktop) 4 else 2,
                        onPerfumeClick = onPerfumeClick,
                    )
                }
        }
    }
}

@Composable
private fun PerfumeGrid(
    perfumes: List<PerfumeSummaryDto>,
    columns: Int,
    onPerfumeClick: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
        perfumes.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                rowItems.forEach { perfume ->
                    PerfumeCard(
                        perfume = perfume.toCardPerfume(),
                        variant = PerfumeCardVariant.Vertical,
                        onClick = { onPerfumeClick(perfume.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                // Complète la dernière ligne pour garder des cartes de même largeur
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ExplorerParAccordSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onAccordClick: (AccordItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 44.dp,
                bottom = 48.dp,
            )
    ) {
        Text(
            text = "Explorer par accord",
            style = MaterialTheme.typography.headlineLarge,
            color = Profond,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Suivez vos sens, trouvez votre famille olfactive.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                lineHeight = 22.sp,
            ),
            color = Secondaire,
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                accordsList.forEach { accord ->
                    AccordCard(
                        accord = accord,
                        onClick = { onAccordClick(accord) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                accordsList.chunked(2).forEach { rowAccords ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        rowAccords.forEach { accord ->
                            AccordCard(
                                accord = accord,
                                onClick = { onAccordClick(accord) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccordCard(
    accord: AccordItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(accord.backgroundColor)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = accord.imageRes),
            contentDescription = accord.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(161.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = accord.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Profond,
            )
        }
    }
}

@Composable
private fun MarquesALaUneSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onBrandClick: (BrandItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Sable)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 44.dp,
                bottom = 52.dp,
            )
    ) {
        Text(
            text = "Marques à la une",
            style = MaterialTheme.typography.headlineLarge,
            color = Profond,
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                featuredBrands.forEach { brand ->
                    BrandItemView(
                        brand = brand,
                        onClick = { onBrandClick(brand) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(36.dp),
            ) {
                featuredBrands.chunked(2).forEach { rowBrands ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        rowBrands.forEach { brand ->
                            BrandItemView(
                                brand = brand,
                                onClick = { onBrandClick(brand) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandItemView(
    brand: BrandItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = brand.name,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = Profond,
        )
        Text(
            text = brand.country,
            style = MaterialTheme.typography.bodyMedium,
            color = Secondaire,
        )
    }
}

@Composable
private fun LeJournalSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onArticleClick: (JournalArticle) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 44.dp,
                bottom = 52.dp,
            )
    ) {
        SectionEyebrow(text = "LE JOURNAL")
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (isDesktop) {
                "Des histoires à fleur de peau"
            } else {
                "Des histoires à fleur de\npeau"
            },
            style = MaterialTheme.typography.headlineLarge.copy(
                lineHeight = 36.sp,
            ),
            color = Profond,
        )
        Spacer(modifier = Modifier.height(28.dp))

        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Top,
            ) {
                JournalArticleCard(
                    article = journalArticles[0],
                    imageHeight = 400.dp,
                    titleFontSize = 24.sp,
                    onClick = { onArticleClick(journalArticles[0]) },
                    modifier = Modifier.weight(2f),
                )
                JournalArticleCard(
                    article = journalArticles[1],
                    imageHeight = 300.dp,
                    titleFontSize = 20.sp,
                    onClick = { onArticleClick(journalArticles[1]) },
                    modifier = Modifier.weight(1f),
                )
                JournalArticleCard(
                    article = journalArticles[2],
                    imageHeight = 300.dp,
                    titleFontSize = 20.sp,
                    onClick = { onArticleClick(journalArticles[2]) },
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(36.dp),
            ) {
                journalArticles.forEach { article ->
                    JournalArticleCard(
                        article = article,
                        imageHeight = 260.dp,
                        titleFontSize = 22.sp,
                        onClick = { onArticleClick(article) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun JournalArticleCard(
    article: JournalArticle,
    imageHeight: Dp,
    titleFontSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(id = article.imageRes),
            contentDescription = article.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clip(RoundedCornerShape(16.dp)),
        )
        Spacer(modifier = Modifier.height(16.dp))
        SectionEyebrow(text = article.category)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = titleFontSize,
                lineHeight = (titleFontSize.value + 6).sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = Profond,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "${article.readTime}  ·  Lire l’article →",
            style = MaterialTheme.typography.bodySmall,
            color = Secondaire,
        )
    }
}

@Composable
private fun CreezVotreCollectionSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onCreateAccountClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Ciel)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 44.dp,
                bottom = 44.dp,
            )
    ) {
        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionEyebrow(
                        text = "VOTRE UNIVERS OLFACTIF",
                        color = Profond,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Créez votre collection",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Profond,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Gardez vos favoris, racontez vos découvertes, cultivez votre curiosité.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Profond,
                    )
                }
                Button(
                    onClick = onCreateAccountClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primaire,
                        contentColor = Blanc,
                    ),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
                    modifier = Modifier.height(50.dp),
                ) {
                    Text(
                        text = "Créer mon compte",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                SectionEyebrow(
                    text = "VOTRE UNIVERS OLFACTIF",
                    color = Profond,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Créez votre collection",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Profond,
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Gardez vos favoris, racontez vos\ndécouvertes, cultivez votre curiosité.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 23.sp,
                    ),
                    color = Profond,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onCreateAccountClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primaire,
                        contentColor = Blanc,
                    ),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
                    modifier = Modifier.height(50.dp),
                ) {
                    Text(
                        text = "Créer mon compte",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun FooterSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onNavigate: (String) -> Unit,
) {
    var email by remember { mutableStateOf("") }

    val footerLinks = listOf(
        "Fragrantica" to Routes.ACCUEIL,
        "À propos" to Routes.A_PROPOS,
        "Contact" to Routes.A_PROPOS,
        "Instagram" to Routes.A_PROPOS,
        "Pinterest" to Routes.A_PROPOS,
        "Mentions légales" to Routes.A_PROPOS,
        "Confidentialité" to Routes.A_PROPOS,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Profond)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 48.dp,
                bottom = 44.dp,
            )
    ) {
        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Un peu de parfum dans votre boîte mail.",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Blanc,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Nos découvertes et histoires olfactives, une fois par mois.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ciel,
                    )
                }
                Spacer(modifier = Modifier.width(48.dp))
                NewsletterInputPill(
                    email = email,
                    onEmailChange = { email = it },
                    modifier = Modifier.width(440.dp),
                )
            }
            Spacer(modifier = Modifier.height(44.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                footerLinks.forEachIndexed { index, (label, route) ->
                    Text(
                        text = label,
                        style = if (index == 0) {
                            MaterialTheme.typography.titleMedium.copy(
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                            )
                        } else {
                            MaterialTheme.typography.bodyMedium
                        },
                        color = if (index == 0) Blanc else Ciel,
                        modifier = Modifier.clickable { onNavigate(route) },
                    )
                }
            }
        } else {
            Text(
                text = "Un peu de parfum dans votre\nboîte mail.",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                ),
                color = Blanc,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Nos découvertes et histoires olfactives, une fois par\nmois.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                ),
                color = Ciel,
            )
            Spacer(modifier = Modifier.height(24.dp))
            NewsletterInputPill(
                email = email,
                onEmailChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(36.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                footerLinks.forEachIndexed { index, (label, route) ->
                    Text(
                        text = label,
                        style = if (index == 0) {
                            MaterialTheme.typography.titleMedium.copy(
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                            )
                        } else {
                            MaterialTheme.typography.bodyMedium
                        },
                        color = if (index == 0) Blanc else Ciel,
                        modifier = Modifier.clickable { onNavigate(route) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "© 2026 Fragrantica · Le parfum est une histoire de partage.",
            style = MaterialTheme.typography.bodySmall,
            color = Ciel.copy(alpha = 0.85f),
        )
    }
}

@Composable
private fun NewsletterInputPill(
    email: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(50))
            .background(Blanc)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (email.isEmpty()) {
                Text(
                    text = "Votre adresse e-mail",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Secondaire,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = email,
                onValueChange = onEmailChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Texte),
                cursorBrush = SolidColor(Primaire),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Text(
            text = "S’inscrire →",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = Primaire,
            modifier = Modifier.clickable { onEmailChange("") },
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 2400)
@Composable
fun AccueilScreenMobilePreview() {
    FragranticaTheme {
        AccueilScreen()
    }
}

@Preview(showBackground = true, widthDp = 1440, heightDp = 2400)
@Composable
fun AccueilScreenDesktopPreview() {
    FragranticaTheme {
        AccueilScreen()
    }
}