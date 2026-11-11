package dev.nohus.rift.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.dynamicportraits.DynamicCharacterPortraitStandings
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.intel_ansiblex_name
import dev.nohus.rift.generated.resources.intel_entity_additional_hostile_count
import dev.nohus.rift.generated.resources.intel_entity_combat_probes_label
import dev.nohus.rift.generated.resources.intel_entity_friendly_count
import dev.nohus.rift.generated.resources.intel_entity_gate_camp_label
import dev.nohus.rift.generated.resources.intel_entity_hostile_count
import dev.nohus.rift.generated.resources.intel_entity_kill_label
import dev.nohus.rift.generated.resources.intel_entity_loss_label
import dev.nohus.rift.generated.resources.intel_entity_npc_corporation_label
import dev.nohus.rift.generated.resources.intel_gate_name
import dev.nohus.rift.generated.resources.intel_label_bubbles
import dev.nohus.rift.generated.resources.intel_label_ess
import dev.nohus.rift.generated.resources.intel_label_no_visual
import dev.nohus.rift.generated.resources.intel_label_skyhook
import dev.nohus.rift.generated.resources.intel_label_spike
import dev.nohus.rift.generated.resources.intel_label_wormhole
import dev.nohus.rift.generated.resources.intel_ship_count
import dev.nohus.rift.generated.resources.keywords_combat_probe
import dev.nohus.rift.generated.resources.keywords_ess
import dev.nohus.rift.generated.resources.keywords_gatecamp
import dev.nohus.rift.generated.resources.keywords_interdiction_probe
import dev.nohus.rift.generated.resources.keywords_killreport
import dev.nohus.rift.generated.resources.keywords_no_visual
import dev.nohus.rift.generated.resources.keywords_skyhook
import dev.nohus.rift.generated.resources.keywords_spike
import dev.nohus.rift.generated.resources.keywords_wormhole
import dev.nohus.rift.intel.state.CharacterBound
import dev.nohus.rift.intel.state.Clearable
import dev.nohus.rift.intel.state.SystemEntity
import dev.nohus.rift.repositories.IdRanges
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.repositories.StarGatesRepository
import dev.nohus.rift.repositories.character.CharacterDetailsRepository.CharacterDetails
import dev.nohus.rift.standings.getColor
import dev.nohus.rift.standings.isFriendly
import dev.nohus.rift.types.TypesRepository
import dev.nohus.rift.utils.openBrowser
import dev.nohus.rift.utils.toURIOrNull
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import java.time.Instant

@Composable
fun SystemEntities(
    entities: List<SystemEntity>,
    system: MapSolarSystem,
    rowHeight: Dp,
    isHorizontal: Boolean = false,
    isGroupingCharacters: Boolean = false,
    isCleared: Boolean = false,
) {
    val modifier = if (isCleared) Modifier.alpha(0.5f) else Modifier
    entities.filterIsInstance<SystemEntity.Killmail>().forEach { killmail ->
        SystemEntityInfoRow(rowHeight, isHorizontal, modifier = modifier) {
            ClickableEntity(
                onClick = { killmail.url.toURIOrNull()?.openBrowser() },
            ) {
                Image(
                    painter = painterResource(Res.drawable.keywords_killreport),
                    contentDescription = null,
                    modifier = Modifier.size(rowHeight),
                )
            }
            if (killmail.ship != null) {
                ClickableShip(killmail.ship) {
                    RiftTooltipArea(
                        text = killmail.ship.name,
                    ) {
                        AsyncTypeIcon(
                            typeId = killmail.ship.id,
                            modifier = Modifier.size(rowHeight),
                        )
                    }
                }
            } else if (killmail.typeName != null) {
                val typedRepository: TypesRepository = remember { koin.get() }
                val type = typedRepository.getType(killmail.typeName)
                RiftTooltipArea(
                    text = killmail.typeName,
                ) {
                    AsyncTypeIcon(
                        type = type,
                        modifier = Modifier.size(rowHeight),
                    )
                }
            }
            if (killmail.victim.characterId != null && killmail.victim.details != null) {
                CharactersPortraits(listOf(killmail.victim.details), rowHeight)
            } else {
                // No character (killmail of a deployable, etc.), show just corporation/alliance
                CharacterMembership(
                    corporationId = killmail.victim.corporationId,
                    corporationName = killmail.victim.corporationName,
                    allianceId = killmail.victim.allianceId,
                    allianceName = killmail.victim.allianceName,
                    rowHeight = rowHeight,
                )
            }

            var style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold)
            killmail.victim.standing?.getColor()?.let { style = style.copy(color = it) }
            val ticket = killmail.victim.allianceTicker ?: if (killmail.victim.corporationId?.isNpcCorp() == true) stringResource(Res.string.intel_entity_npc_corporation_label) else killmail.victim.corporationTicker ?: ""
            if (rowHeight < 32.dp) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    modifier = Modifier.padding(horizontal = Spacing.small),
                ) {
                    Text(
                        text = ticket,
                        style = RiftTheme.typography.bodySecondary,
                    )
                    Text(
                        text = if (killmail.victim.standing?.isFriendly == true) stringResource(Res.string.intel_entity_loss_label) else stringResource(Res.string.intel_entity_kill_label),
                        style = style,
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = Spacing.small),
                ) {
                    Text(
                        text = if (killmail.victim.standing?.isFriendly == true) stringResource(Res.string.intel_entity_loss_label) else stringResource(Res.string.intel_entity_kill_label),
                        style = style,
                    )
                    Text(
                        text = ticket,
                        style = RiftTheme.typography.detailSecondary,
                    )
                }
            }
        }
    }
    entities.filterIsInstance<SystemEntity.Ship>().forEach { ship ->
        ClickableShip(ship.type) {
            SystemEntityInfoRow(rowHeight, isHorizontal, modifier = modifier) {
                AsyncTypeIcon(
                    typeId = ship.type.id,
                    modifier = Modifier.size(rowHeight),
                )

                var nameStyle = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold)
                ship.standing?.getColor()?.let { nameStyle = nameStyle.copy(color = it) }

                val text = if (ship.count > 1) {
                    stringResource(Res.string.intel_ship_count, ship.count, ship.type.name)
                } else {
                    ship.type.name
                }
                Text(
                    text = text,
                    style = nameStyle,
                    modifier = Modifier.padding(4.dp),
                )
            }
        }
    }
    if (isGroupingCharacters) {
        entities.filterIsInstance<SystemEntity.Character>()
            .groupBy {
                it.details.allianceId ?: if (it.details.corporationId.isNpcCorp()) 1 else it.details.corporationId
            }
            .forEach { (_, characters) ->
                SystemEntityInfoRow(rowHeight, isHorizontal, modifier = modifier) {
                    CharactersPortraits(characters.map { it.details }, rowHeight)

                    val representative = characters.first().details
                    val characterText = if (representative.standingLevel.isFriendly) {
                        pluralStringResource(Res.plurals.intel_entity_friendly_count, characters.size, characters.size)
                    } else {
                        pluralStringResource(Res.plurals.intel_entity_hostile_count, characters.size, characters.size)
                    }
                    var style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold)
                    representative.standingLevel.getColor()?.let { style = style.copy(color = it) }
                    val ticker = representative.allianceTicker ?: if (representative.corporationId.isNpcCorp()) stringResource(Res.string.intel_entity_npc_corporation_label) else representative.corporationTicker
                    if (rowHeight < 32.dp) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                            modifier = Modifier.padding(horizontal = Spacing.small),
                        ) {
                            Text(
                                text = ticker,
                                style = RiftTheme.typography.bodySecondary,
                            )
                            Text(
                                text = characterText,
                                style = style,
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.padding(horizontal = 4.dp),
                        ) {
                            Text(
                                text = characterText,
                                style = style,
                            )
                            Text(
                                text = ticker,
                                style = RiftTheme.typography.detailSecondary,
                            )
                        }
                    }
                }
            }
    } else {
        entities.filterIsInstance<SystemEntity.Character>()
            .sortedWith(compareBy({ it.details.allianceId }, { it.details.corporationId }))
            .forEach { character ->
                SystemEntityInfoRow(rowHeight, isHorizontal, modifier = modifier) {
                    CharacterDetails(character.details, rowHeight, isAnimated = true)
                }
            }
    }
    entities.firstOrNull { it is SystemEntity.UnspecifiedCharacter }?.let {
        val hasNamedHostiles =
            (entities.filterIsInstance<SystemEntity.Character>() + entities.filterIsInstance<SystemEntity.Ship>()).isNotEmpty()
        val unspecified = it as SystemEntity.UnspecifiedCharacter
        val content = @Composable {
            Text(
                text = if (hasNamedHostiles) {
                    pluralStringResource(Res.plurals.intel_entity_additional_hostile_count, unspecified.count, unspecified.count)
                } else {
                    pluralStringResource(Res.plurals.intel_entity_hostile_count, unspecified.count, unspecified.count)
                },
                style = RiftTheme.typography.bodyHighlighted,
                modifier = Modifier.padding(4.dp),
            )
        }
        if (isHorizontal) {
            SystemEntityInfoRow(rowHeight, true, modifier = modifier) {
                content()
            }
        } else {
            Row(modifier = modifier) {
                content()
            }
        }
    }
    entities.forEach { entity ->
        when (entity) {
            SystemEntity.Bubbles -> IconInfoRow(Res.drawable.keywords_interdiction_probe, stringResource(Res.string.intel_label_bubbles), rowHeight, isHorizontal, modifier)
            SystemEntity.CombatProbes -> IconInfoRow(Res.drawable.keywords_combat_probe, stringResource(Res.string.intel_entity_combat_probes_label), rowHeight, isHorizontal, modifier)
            SystemEntity.Ess -> IconInfoRow(Res.drawable.keywords_ess, stringResource(Res.string.intel_label_ess), rowHeight, isHorizontal, modifier)
            SystemEntity.Skyhook -> IconInfoRow(Res.drawable.keywords_skyhook, stringResource(Res.string.intel_label_skyhook), rowHeight, isHorizontal, modifier)
            is SystemEntity.Gate -> GateInfoRow(system, entity, rowHeight, isHorizontal, modifier)
            is SystemEntity.Celestial -> CelestialInfoRow(entity, rowHeight, isHorizontal, modifier)
            SystemEntity.GateCamp -> IconInfoRow(Res.drawable.keywords_gatecamp, stringResource(Res.string.intel_entity_gate_camp_label), rowHeight, isHorizontal, modifier)
            SystemEntity.NoVisual -> NoVisualRow(rowHeight, isHorizontal, modifier)
            SystemEntity.Spike -> IconInfoRow(Res.drawable.keywords_spike, stringResource(Res.string.intel_label_spike), rowHeight, isHorizontal, modifier)
            SystemEntity.Wormhole -> WormholeInfoRow(rowHeight, isHorizontal, modifier)
            is SystemEntity.Character -> {}
            is SystemEntity.UnspecifiedCharacter -> {}
            is SystemEntity.Ship -> {}
            is SystemEntity.Killmail -> {}
            is CharacterBound -> {}
            is Clearable -> {}
        }
    }
}

@Composable
private fun CharactersPortraits(
    characters: List<CharacterDetails>,
    rowHeight: Dp,
) {
    Row {
        if (characters.size > 3) {
            val enterTimestamp = remember { Instant.now() }
            InfiniteScrollingCarousel(
                items = characters,
                delay = 2_000,
                modifier = Modifier.height(rowHeight).width(rowHeight * 3),
            ) { character ->
                ClickableCharacter(character.characterId) {
                    CharacterTooltip(character) {
                        DynamicCharacterPortraitStandings(
                            characterId = character.characterId,
                            size = rowHeight,
                            standingLevel = character.standingLevel,
                            enterTimestamp = enterTimestamp,
                            isAnimated = true,
                        )
                    }
                }
            }
        } else {
            Row {
                characters.forEach { character ->
                    ClickableCharacter(character.characterId) {
                        CharacterTooltip(character) {
                            val now = remember(character.characterId) { Instant.now() }
                            DynamicCharacterPortraitStandings(
                                characterId = character.characterId,
                                size = rowHeight,
                                standingLevel = character.standingLevel,
                                isAnimated = true,
                                enterTimestamp = now,
                            )
                        }
                    }
                }
            }
        }
    }
    val representative = characters.first()
    CharacterMembership(
        corporationId = representative.corporationId,
        corporationName = representative.corporationName,
        allianceId = representative.allianceId,
        allianceName = representative.allianceName,
        rowHeight = rowHeight,
    )
}

/**
 * Logo of alliance or corporation (if no alliance)
 */
@Composable
private fun CharacterMembership(
    corporationId: Int?,
    corporationName: String?,
    allianceId: Int?,
    allianceName: String?,
    rowHeight: Dp,
) {
    if (allianceId != null) {
        ClickableAlliance(allianceId) {
            RiftTooltipArea(
                text = allianceName ?: "",
            ) {
                AsyncAllianceLogo(
                    allianceId = allianceId,
                    size = 32,
                    modifier = Modifier.size(rowHeight),
                )
            }
        }
    } else if (corporationId != null) {
        ClickableCorporation(corporationId) {
            RiftTooltipArea(
                text = corporationName ?: "",
            ) {
                AsyncCorporationLogo(
                    corporationId = corporationId,
                    size = 32,
                    modifier = Modifier.size(rowHeight),
                )
            }
        }
    }
}

@Composable
fun SystemEntityInfoRow(
    rowHeight: Dp,
    hasBorder: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val border = RiftTheme.colors.borderGreyLight
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(IntrinsicSize.Max)
            .heightIn(min = rowHeight)
            .modifyIf(hasBorder) { Modifier.border(1.dp, border) },
        content = content,
    )
}

@Composable
private fun IconInfoRow(icon: DrawableResource, text: String, rowHeight: Dp, isHorizontal: Boolean, modifier: Modifier = Modifier) {
    SystemEntityInfoRow(rowHeight, isHorizontal, modifier = modifier) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(rowHeight),
        )
        Text(
            text = text,
            style = RiftTheme.typography.bodyPrimary,
            modifier = Modifier.padding(4.dp),
        )
    }
}

@Composable
private fun WormholeInfoRow(rowHeight: Dp, isHorizontal: Boolean, modifier: Modifier = Modifier) {
    SystemEntityInfoRow(rowHeight, isHorizontal, modifier = modifier) {
        val transition = rememberInfiniteTransition()
        val rotation by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(60_000, easing = LinearEasing)),
        )
        Image(
            painter = painterResource(Res.drawable.keywords_wormhole),
            contentDescription = null,
            modifier = Modifier
                .size(rowHeight)
                .rotate(-rotation),
        )
        Text(
            text = stringResource(Res.string.intel_label_wormhole),
            style = RiftTheme.typography.bodyPrimary,
            modifier = Modifier.padding(4.dp),
        )
    }
}

@Composable
private fun GateInfoRow(system: MapSolarSystem, entity: SystemEntity.Gate, rowHeight: Dp, isHorizontal: Boolean, modifier: Modifier = Modifier) {
    val starGatesRepository: StarGatesRepository = remember { koin.get() }
    val gate = starGatesRepository.getGate(entity.isAnsiblex, system.id, entity.system2.id)
    val name = if (entity.isAnsiblex) {
        stringResource(Res.string.intel_ansiblex_name, entity.system2.name)
    } else {
        stringResource(Res.string.intel_gate_name, entity.system2.name)
    }
    ClickableLocation(
        systemId = system.id,
        locationId = gate.locationId,
        locationTypeId = gate.typeId,
        locationName = name,
    ) {
        SystemEntityInfoRow(rowHeight, isHorizontal, modifier = modifier) {
            AsyncTypeIcon(
                typeId = gate.typeId,
                modifier = Modifier.size(rowHeight),
            )
            VerticalDivider(color = RiftTheme.colors.borderGreyLight, modifier = Modifier.height(rowHeight))
            Column(
                modifier = Modifier.padding(horizontal = Spacing.small),
            ) {
                Text(
                    text = name,
                    style = RiftTheme.typography.bodyHighlighted,
                )
                if (entity.distanceKm != null && rowHeight >= 32.dp) {
                    Text(
                        text = "${entity.distanceKm}km",
                        style = RiftTheme.typography.detailSecondary,
                    )
                }
            }
            if (entity.distanceKm != null && rowHeight < 32.dp) {
                Text(
                    text = "${entity.distanceKm}km",
                    style = RiftTheme.typography.bodySecondary,
                    modifier = Modifier.padding(4.dp),
                )
            }
        }
    }
}

@Composable
private fun CelestialInfoRow(entity: SystemEntity.Celestial, rowHeight: Dp, isHorizontal: Boolean, modifier: Modifier = Modifier) {
    ClickableLocation(
        systemId = entity.celestial.solarSystemId,
        locationId = entity.celestial.id.toLong(),
        locationTypeId = entity.celestial.type.id,
        locationName = entity.celestial.name,
    ) {
        SystemEntityInfoRow(rowHeight, isHorizontal, modifier = modifier) {
            AsyncTypeIcon(
                type = entity.celestial.type,
                modifier = Modifier.size(rowHeight),
            )

            VerticalDivider(color = RiftTheme.colors.borderGreyLight, modifier = Modifier.height(rowHeight))
            Column(
                modifier = Modifier.padding(horizontal = Spacing.small),
            ) {
                Text(
                    text = entity.celestial.name,
                    style = RiftTheme.typography.bodyHighlighted,
                )
                if (rowHeight >= 32.dp) {
                    Text(
                        text = "${entity.distanceKm}km",
                        style = RiftTheme.typography.detailSecondary,
                    )
                }
            }
            if (rowHeight < 32.dp) {
                Text(
                    text = "${entity.distanceKm}km",
                    style = RiftTheme.typography.bodySecondary,
                    modifier = Modifier.padding(4.dp),
                )
            }
        }
    }
}

@Composable
private fun NoVisualRow(
    rowHeight: Dp,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier,
) {
    val content = @Composable {
        Image(
            painter = painterResource(Res.drawable.keywords_no_visual),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = stringResource(Res.string.intel_label_no_visual),
            style = RiftTheme.typography.detailPrimary,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
    if (isHorizontal) {
        if (rowHeight < 32.dp) {
            SystemEntityInfoRow(rowHeight, true, modifier = modifier) {
                Spacer(Modifier.width(Spacing.verySmall))
                content()
            }
        } else {
            SystemEntityInfoRow(rowHeight, true, modifier = modifier) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    content()
                }
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.height(IntrinsicSize.Max).heightIn(min = 16.dp),
        ) {
            content()
        }
    }
}

private fun Int.isNpcCorp(): Boolean = IdRanges.isNpcCorporation(this)
