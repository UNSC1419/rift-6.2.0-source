package dev.nohus.rift.intelscanner.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.AsyncAllianceLogo
import dev.nohus.rift.compose.AsyncCorporationLogo
import dev.nohus.rift.compose.ClickableAlliance
import dev.nohus.rift.compose.ClickableCorporation
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.animatedcontentfixed.AnimatedVisibility
import dev.nohus.rift.compose.modifyIf
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.esi_logo_16
import dev.nohus.rift.generated.resources.intel_scanner_character_count
import dev.nohus.rift.generated.resources.intel_scanner_entity_character_count
import dev.nohus.rift.generated.resources.intel_scanner_loading_count
import dev.nohus.rift.generated.resources.intel_scanner_source_loading_count
import dev.nohus.rift.generated.resources.intel_scanner_summary_alliances
import dev.nohus.rift.generated.resources.intel_scanner_summary_corporations
import dev.nohus.rift.generated.resources.intel_scanner_summary_factions
import dev.nohus.rift.generated.resources.menu_zkillboard
import dev.nohus.rift.intelscanner.IntelScannerViewModel.IntelScannerSettings
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.CharacterItem
import dev.nohus.rift.intelscanner.LoadCharactersIntelUseCase.CharacterState
import dev.nohus.rift.utils.formatNumber
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun IntelScannerSummary(
    characters: List<CharacterItem>,
    settings: IntelScannerSettings,
) {
    val loadedCharacters = remember(characters) {
        characters.mapNotNull { it.state as? CharacterState.Loaded }
    }
    val corporations = remember(loadedCharacters) {
        loadedCharacters
            .groupBy { it.corporationId }
            .map { (id, characters) -> SummaryEntity(id, characters.first().corporationName, characters.size) }
            .sortedWith(compareByDescending<SummaryEntity> { it.count }.thenBy { it.name })
    }
    val alliances = remember(loadedCharacters) {
        loadedCharacters
            .filter { it.allianceId != null }
            .groupBy { it.allianceId!! }
            .map { (id, characters) -> SummaryEntity(id, characters.first().allianceName.orEmpty(), characters.size) }
            .sortedWith(compareByDescending<SummaryEntity> { it.count }.thenBy { it.name })
    }
    val factions = remember(loadedCharacters) {
        loadedCharacters
            .filter { it.factionId != null }
            .groupBy { it.factionId!! }
            .map { (id, characters) -> SummaryEntity(id, characters.first().factionName.orEmpty(), characters.size) }
            .sortedWith(compareByDescending<SummaryEntity> { it.count }.thenBy { it.name })
    }
    val esiLoadingCount = characters.count { it.state == CharacterState.Loading }
    val zKillboardLoadingCount = characters.count { character ->
        when (val state = character.state) {
            CharacterState.Loading -> true
            is CharacterState.Loaded -> state.isZKillboardLoading
            CharacterState.Error, CharacterState.NotFound -> false
        }
    }
    val isShowingAlliances = settings.isShowingAlliancesSummary && alliances.isNotEmpty()
    val isShowingCorporations = settings.isShowingCorporationsSummary && corporations.isNotEmpty()
    val isShowingFactions = settings.isShowingFactionWarfareSummary && factions.isNotEmpty()

    if (settings.isShowingCharactersSummary || isShowingAlliances || isShowingCorporations || isShowingFactions) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.small),
        ) {
            if (settings.isShowingCharactersSummary) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .modifyIf(isShowingFactions || isShowingAlliances || isShowingCorporations) {
                            height(settings.portraitSize + 12.dp)
                        },
                ) {
                    Text(
                        text = pluralStringResource(Res.plurals.intel_scanner_character_count, characters.size, formatNumber(characters.size)),
                        style = RiftTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        LoadingCount("ESI", Res.drawable.esi_logo_16, esiLoadingCount)
                        LoadingCount("zKillboard.com", Res.drawable.menu_zkillboard, zKillboardLoadingCount)
                    }
                }
            }

            if (isShowingFactions) {
                SummaryGroup(title = stringResource(Res.string.intel_scanner_summary_factions), entities = factions) { entity ->
                    SummaryEntityLogo(entity) {
                        AsyncCorporationLogo(
                            corporationId = entity.id,
                            size = 64,
                            modifier = Modifier.size(settings.portraitSize),
                        )
                    }
                }
            }
            if (isShowingAlliances) {
                SummaryGroup(
                    title = stringResource(Res.string.intel_scanner_summary_alliances),
                    entities = alliances.take(settings.maxAlliancesInSummary),
                    maxLines = 1,
                ) { entity ->
                    ClickableAlliance(entity.id) {
                        SummaryEntityLogo(entity) {
                            AsyncAllianceLogo(
                                allianceId = entity.id,
                                size = 64,
                                modifier = Modifier.size(settings.portraitSize),
                            )
                        }
                    }
                }
            }
            if (isShowingCorporations) {
                SummaryGroup(
                    title = stringResource(Res.string.intel_scanner_summary_corporations),
                    entities = corporations.take(settings.maxCorporationsInSummary),
                    maxLines = 1,
                ) { entity ->
                    ClickableCorporation(entity.id) {
                        SummaryEntityLogo(entity) {
                            AsyncCorporationLogo(
                                corporationId = entity.id,
                                size = 64,
                                modifier = Modifier.size(settings.portraitSize),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingCount(
    dataSource: String,
    logo: DrawableResource,
    count: Int,
) {
    val transition = rememberInfiniteTransition()
    val shimmerPosition by transition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_200, easing = LinearEasing),
        ),
    )
    AnimatedVisibility(count > 0) {
        RiftTooltipArea(pluralStringResource(Res.plurals.intel_scanner_source_loading_count, count, dataSource, formatNumber(count))) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                modifier = Modifier
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        val shimmerWidth = size.width * 0.35f
                        val shimmerCenter = size.width * shimmerPosition
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.White.copy(alpha = 0.35f),
                                    Color.Transparent,
                                ),
                                start = Offset(shimmerCenter - shimmerWidth, 0f),
                                end = Offset(shimmerCenter + shimmerWidth, size.height),
                            ),
                            blendMode = BlendMode.SrcAtop,
                        )
                    },
            ) {
                Image(
                    painter = painterResource(logo),
                    contentDescription = null,
                    modifier = Modifier.height(16.dp),
                )
                Text(
                    text = pluralStringResource(Res.plurals.intel_scanner_loading_count, count, formatNumber(count)),
                    style = RiftTheme.typography.bodySecondary,
                )
            }
        }
    }
}

@Composable
private fun SummaryGroup(
    title: String,
    entities: List<SummaryEntity>,
    maxLines: Int = Int.MAX_VALUE,
    content: @Composable (SummaryEntity) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Text(
            text = title,
            style = RiftTheme.typography.bodySecondary,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
            maxLines = maxLines,
        ) {
            for (entity in entities) {
                key(entity.id) {
                    content(entity)
                }
            }
        }
    }
}

@Composable
private fun SummaryEntityLogo(
    entity: SummaryEntity,
    logo: @Composable () -> Unit,
) {
    RiftTooltipArea(
        text = pluralStringResource(Res.plurals.intel_scanner_entity_character_count, entity.count, entity.name, formatNumber(entity.count)),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            logo()
            Text(
                text = entity.count.toString(),
                style = RiftTheme.typography.detailSecondary.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

private data class SummaryEntity(
    val id: Int,
    val name: String,
    val count: Int,
)
