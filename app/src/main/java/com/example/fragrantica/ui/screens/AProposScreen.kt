package com.example.fragrantica.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.navigation.NavHostController
import com.example.fragrantica.R
import com.example.fragrantica.navigation.Routes
import com.fragrantica.ui.theme.Blanc
import com.fragrantica.ui.theme.Chaux
import com.fragrantica.ui.theme.Ciel
import com.fragrantica.ui.theme.FragranticaTheme
import com.fragrantica.ui.theme.Manrope
import com.fragrantica.ui.theme.Primaire
import com.fragrantica.ui.theme.Profond
import com.fragrantica.ui.theme.Sable
import com.fragrantica.ui.theme.Secondaire
import com.fragrantica.ui.theme.Texte

private data class ValuePillar(
    val title: String,
    val description: String,
)

private data class KeyFigure(
    val number: String,
    val label: String,
)

private data class HowItWorksStep(
    val number: String,
    val title: String,
    val description: String,
)

private data class FaqEntry(
    val question: String,
    val answer: String,
)

private val valuePillars = listOf(
    ValuePillar(
        title = "Curiosité",
        description = "Ouvrir des portes, des matières aux maisons, des grands classiques aux signatures confidentielles.",
    ),
    ValuePillar(
        title = "Partage",
        description = "Faire place aux expériences et aux sensibilités, sans imposer une seule façon d’aimer.",
    ),
    ValuePillar(
        title = "Clarté",
        description = "Rendre les informations utiles et lisibles, pour laisser plus de place à la découverte.",
    ),
)

private val keyFigures = listOf(
    KeyFigure(number = "100 000+", label = "parfums"),
    KeyFigure(number = "5 000+", label = "marques"),
    KeyFigure(number = "2 000+", label = "notes olfactives"),
)

private val howItWorksSteps = listOf(
    HowItWorksStep(
        number = "01",
        title = "Explorez",
        description = "Recherchez une note, une marque ou un parfum.",
    ),
    HowItWorksStep(
        number = "02",
        title = "Découvrez",
        description = "Lisez les accords, la pyramide et les avis.",
    ),
    HowItWorksStep(
        number = "03",
        title = "Collectionnez",
        description = "Gardez vos parfums et vos envies au même endroit.",
    ),
    HowItWorksStep(
        number = "04",
        title = "Partagez",
        description = "Racontez votre expérience à la communauté.",
    ),
)

private val faqEntries = listOf(
    FaqEntry(
        question = "Faut-il un compte pour explorer ?",
        answer = "Oui. Les fiches parfums et le journal ne sont pas accessibles librement. Un compte permet de créer votre collection et d’enregistrer vos favoris et consulter la bibliothèque de parfum.",
    ),
    FaqEntry(
        question = "Comment sont calculées les notes ?",
        answer = "Les notes correspondent à la moyenne des évaluations attribuées par les membres de la communauté sur chaque fiche parfum.",
    ),
    FaqEntry(
        question = "Puis-je suggérer un parfum manquant ?",
        answer = "Oui, vous pouvez nous transmettre les informations du parfum via le formulaire de contact ci-dessous afin qu’il soit ajouté à la base.",
    ),
    FaqEntry(
        question = "Fragrantica vend-il des parfums ?",
        answer = "Non. Fragrantica est une encyclopédie indépendante et un espace communautaire dédié à la découverte olfactive, sans vente directe.",
    ),
)

@Composable
fun AProposScreen(
    navController: NavHostController? = null,
    modifier: Modifier = Modifier,
) {
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
            AProposHeroSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
            )

            AProposValeursSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
            )

            AProposChiffresSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
            )

            AProposCommentCaMarcheSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
            )

            AProposFaqSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
            )

            AProposContactSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
            )

            AProposFooterSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onNavigate = navigateTo,
            )
        }
    }
}

@Composable
private fun AProposEyebrow(
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
private fun AProposHeroSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isDesktop) 40.dp else 16.dp,
                bottom = if (isDesktop) 64.dp else 56.dp,
            )
    ) {
        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(56.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp),
                ) {
                    AProposEyebrow(text = "À PROPOS DE FRAGRANTICA")
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Le parfum est\nun langage.\nExplorons-le\nensemble.",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 52.sp,
                            lineHeight = 60.sp,
                        ),
                        color = Profond,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Une encyclopédie vivante, un journal curieux et une communauté réunie par les émotions que les parfums éveillent.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Secondaire,
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.img_figue),
                    contentDescription = "Feuilles de figuier au soleil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .height(600.dp)
                        .clip(RoundedCornerShape(20.dp)),
                )
            }
        } else {
            AProposEyebrow(text = "À PROPOS DE FRAGRANTICA")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Le parfum est\nun langage.\nExplorons-le\nensemble.",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 38.sp,
                    lineHeight = 46.sp,
                ),
                color = Profond,
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Une encyclopédie vivante, un journal curieux\net une communauté réunie par les émotions\nque les parfums éveillent.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                ),
                color = Secondaire,
            )
            Spacer(modifier = Modifier.height(28.dp))
            Image(
                painter = painterResource(id = R.drawable.img_figue),
                contentDescription = "Feuilles de figuier au soleil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(528.dp)
                    .clip(RoundedCornerShape(20.dp)),
            )
        }
    }
}

@Composable
private fun AProposValeursSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanc)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isDesktop) 64.dp else 44.dp,
                bottom = if (isDesktop) 64.dp else 48.dp,
            )
    ) {
        Text(
            text = if (isDesktop) {
                "Rendre le parfum plus accessible,\nsans lui enlever son mystère."
            } else {
                "Rendre le parfum plus\naccessible,\nsans lui enlever son\nmystère."
            },
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = if (isDesktop) 34.sp else 28.sp,
                lineHeight = if (isDesktop) 42.sp else 36.sp,
            ),
            color = Profond,
        )
        Spacer(modifier = Modifier.height(if (isDesktop) 36.dp else 28.dp))

        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(40.dp),
                verticalAlignment = Alignment.Top,
            ) {
                valuePillars.forEach { pillar ->
                    ValuePillarItem(
                        pillar = pillar,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(28.dp),
            ) {
                valuePillars.forEach { pillar ->
                    ValuePillarItem(
                        pillar = pillar,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ValuePillarItem(
    pillar: ValuePillar,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = pillar.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = Profond,
        )
        Text(
            text = pillar.description,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                lineHeight = 23.sp,
            ),
            color = Secondaire,
        )
    }
}

@Composable
private fun AProposChiffresSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Ciel)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isDesktop) 56.dp else 44.dp,
                bottom = if (isDesktop) 56.dp else 44.dp,
            )
    ) {
        AProposEyebrow(
            text = "UN UNIVERS À EXPLORER",
            color = Profond,
        )
        Spacer(modifier = Modifier.height(if (isDesktop) 28.dp else 24.dp))

        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.Top,
            ) {
                keyFigures.forEach { item ->
                    KeyFigureItem(
                        item = item,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                keyFigures.forEach { item ->
                    KeyFigureItem(
                        item = item,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(if (isDesktop) 32.dp else 28.dp))
        Text(
            text = "Chiffres illustratifs pour la maquette · à remplacer par les données validées.",
            style = MaterialTheme.typography.bodySmall.copy(
                lineHeight = 18.sp,
            ),
            color = Profond,
        )
    }
}

@Composable
private fun KeyFigureItem(
    item: KeyFigure,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = item.number,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 42.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = Profond,
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
            ),
            color = Profond,
        )
    }
}

@Composable
private fun AProposCommentCaMarcheSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isDesktop) 64.dp else 44.dp,
                bottom = if (isDesktop) 64.dp else 48.dp,
            )
    ) {
        Text(
            text = "Comment ça marche ?",
            style = MaterialTheme.typography.headlineLarge,
            color = Profond,
        )
        Spacer(modifier = Modifier.height(if (isDesktop) 32.dp else 28.dp))

        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.Top,
            ) {
                howItWorksSteps.forEach { step ->
                    HowItWorksStepItem(
                        step = step,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(28.dp),
            ) {
                howItWorksSteps.forEach { step ->
                    HowItWorksStepItem(
                        step = step,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun HowItWorksStepItem(
    step: HowItWorksStep,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = step.number,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp,
            ),
            color = Primaire,
        )
        Text(
            text = step.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = Profond,
        )
        Text(
            text = step.description,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                lineHeight = 23.sp,
            ),
            color = Secondaire,
        )
    }
}

@Composable
private fun AProposFaqSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
) {
    var expandedIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanc)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isDesktop) 64.dp else 44.dp,
                bottom = if (isDesktop) 64.dp else 48.dp,
            )
    ) {
        Text(
            text = "Questions fréquentes",
            style = MaterialTheme.typography.headlineLarge,
            color = Profond,
        )
        Spacer(modifier = Modifier.height(if (isDesktop) 32.dp else 28.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            faqEntries.forEachIndexed { index, entry ->
                val isExpanded = expandedIndex == index
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedIndex = if (isExpanded) -1 else index
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = entry.question,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 17.sp,
                                lineHeight = 23.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = Profond,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp),
                        )
                        Text(
                            text = if (isExpanded) "−" else "+",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            color = Primaire,
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Text(
                            text = entry.answer,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                lineHeight = 23.sp,
                            ),
                            color = Secondaire,
                            modifier = Modifier.padding(top = 12.dp, end = 24.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AProposContactSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isDesktop) 64.dp else 44.dp,
                bottom = if (isDesktop) 64.dp else 48.dp,
            )
    ) {
        Text(
            text = "Parlons parfum.",
            style = MaterialTheme.typography.headlineLarge,
            color = Profond,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Une suggestion, une question, une belle découverte à partager ?",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                lineHeight = 23.sp,
            ),
            color = Secondaire,
        )
        Spacer(modifier = Modifier.height(28.dp))

        val fieldModifier = if (isDesktop) {
            Modifier.widthIn(max = 630.dp).fillMaxWidth()
        } else {
            Modifier.fillMaxWidth()
        }

        Text(
            text = "Votre e-mail",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = Profond,
        )
        Spacer(modifier = Modifier.height(10.dp))
        ContactTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "vous@exemple.fr",
            modifier = fieldModifier,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Votre message",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = Profond,
        )
        Spacer(modifier = Modifier.height(10.dp))
        ContactTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = "Dites-nous tout...",
            modifier = fieldModifier,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                email = ""
                message = ""
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primaire,
                contentColor = Blanc,
            ),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
            modifier = Modifier.height(50.dp),
        ) {
            Text(
                text = "Envoyer mon message",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Vos données sont utilisées uniquement pour vous répondre.",
            style = MaterialTheme.typography.bodySmall,
            color = Secondaire,
        )
    }
}

@Composable
private fun ContactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Blanc)
            .border(1.dp, Sable, RoundedCornerShape(14.dp))
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = Secondaire,
                maxLines = 1,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Texte),
            cursorBrush = SolidColor(Primaire),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AProposFooterSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onNavigate: (String) -> Unit,
) {
    var newsletterEmail by remember { mutableStateOf("") }

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
                AProposNewsletterPill(
                    email = newsletterEmail,
                    onEmailChange = { newsletterEmail = it },
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
            AProposNewsletterPill(
                email = newsletterEmail,
                onEmailChange = { newsletterEmail = it },
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
private fun AProposNewsletterPill(
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
fun AProposScreenMobilePreview() {
    FragranticaTheme {
        AProposScreen()
    }
}

@Preview(showBackground = true, widthDp = 1440, heightDp = 2400)
@Composable
fun AProposScreenDesktopPreview() {
    FragranticaTheme {
        AProposScreen()
    }
}
