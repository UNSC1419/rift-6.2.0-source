package dev.nohus.rift.structures.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.LoadingBox
import dev.nohus.rift.compose.LoadingSpinnerAmbient
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.VerticalGrid
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.structures_loading_details_title
import dev.nohus.rift.generated.resources.structures_loading_mercenary_dens_complete
import dev.nohus.rift.generated.resources.structures_loading_mercenary_dens_progress
import dev.nohus.rift.generated.resources.structures_loading_mercenary_dens_title
import dev.nohus.rift.generated.resources.structures_loading_skyhooks_complete
import dev.nohus.rift.generated.resources.structures_loading_skyhooks_progress
import dev.nohus.rift.generated.resources.structures_loading_skyhooks_title
import dev.nohus.rift.generated.resources.structures_loading_sovereignty_hubs_complete
import dev.nohus.rift.generated.resources.structures_loading_sovereignty_hubs_progress
import dev.nohus.rift.generated.resources.structures_loading_sovereignty_hubs_title
import dev.nohus.rift.generated.resources.structures_mercenary_dens_tab
import dev.nohus.rift.generated.resources.structures_skyhooks_tab
import dev.nohus.rift.generated.resources.structures_sovereignty_hubs_tab
import dev.nohus.rift.structures.EquinoxStructuresRepository
import dev.nohus.rift.structures.EquinoxStructuresRepository.LoadingStage
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun StructuresLoadingProgress(
    loading: EquinoxStructuresRepository.LoadingState,
    stage: LoadingStage,
) {
    ScrollbarColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(Spacing.large),
    ) {
        LoadingSpinnerAmbient()
        Spacer(Modifier.height(Spacing.medium))

        val title = when (stage) {
            LoadingStage.LoadingMercenaryDens -> stringResource(Res.string.structures_loading_mercenary_dens_title)
            LoadingStage.LoadingSkyhooks -> stringResource(Res.string.structures_loading_skyhooks_title)
            LoadingStage.LoadingSovHubs -> stringResource(Res.string.structures_loading_sovereignty_hubs_title)
            LoadingStage.LoadingDetails -> stringResource(Res.string.structures_loading_details_title)
        }
        Text(
            text = title,
            style = RiftTheme.typography.headlinePrimary,
        )

        Spacer(Modifier.height(Spacing.large))

        VerticalGrid(
            minColumnWidth = 300.dp,
            horizontalSpacing = Spacing.medium,
            verticalSpacing = Spacing.medium,
        ) {
            if (loading.mercenaryDensTotal != null && loading.mercenaryDensTotal > 0) {
                val mercenaryDensPercentage = (loading.mercenaryDensLoaded.toFloat() / loading.mercenaryDensTotal).coerceIn(0f, 1f)
                LoadingBox(mercenaryDensPercentage) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncTypeIcon(
                            typeId = 85230,
                            modifier = Modifier.size(48.dp),
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.small),
                        ) {
                            Text(
                                text = stringResource(Res.string.structures_mercenary_dens_tab),
                                style = RiftTheme.typography.bodyPrimary,
                            )

                            Divider(color = Color.White.copy(alpha = 0.15f))

                            val text = if (loading.mercenaryDensLoaded == loading.mercenaryDensTotal) {
                                pluralStringResource(Res.plurals.structures_loading_mercenary_dens_complete, loading.mercenaryDensLoaded, loading.mercenaryDensLoaded)
                            } else {
                                pluralStringResource(Res.plurals.structures_loading_mercenary_dens_progress, loading.mercenaryDensTotal, loading.mercenaryDensLoaded, loading.mercenaryDensTotal)
                            }
                            Text(
                                text = text,
                                style = if (loading.mercenaryDensLoaded == loading.mercenaryDensTotal) RiftTheme.typography.bodySecondary else RiftTheme.typography.bodyPrimary,
                            )
                        }
                    }
                }
            }

            if (loading.skyhooksTotal != null && loading.skyhooksTotal > 0) {
                val skyhooksPercentage = (loading.skyhooksLoaded.toFloat() / loading.skyhooksTotal).coerceIn(0f, 1f)
                LoadingBox(skyhooksPercentage) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncTypeIcon(
                            typeId = 81080,
                            modifier = Modifier.size(48.dp),
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.small),
                        ) {
                            Text(
                                text = stringResource(Res.string.structures_skyhooks_tab),
                                style = RiftTheme.typography.bodyPrimary,
                            )

                            Divider(color = Color.White.copy(alpha = 0.15f))

                            val text = if (loading.skyhooksLoaded == loading.skyhooksTotal) {
                                pluralStringResource(Res.plurals.structures_loading_skyhooks_complete, loading.skyhooksLoaded, loading.skyhooksLoaded)
                            } else {
                                pluralStringResource(Res.plurals.structures_loading_skyhooks_progress, loading.skyhooksTotal, loading.skyhooksLoaded, loading.skyhooksTotal)
                            }
                            Text(
                                text = text,
                                style = if (loading.skyhooksLoaded == loading.skyhooksTotal) RiftTheme.typography.bodySecondary else RiftTheme.typography.bodyPrimary,
                            )
                        }
                    }
                }
            }

            if (loading.sovHubsTotal != null && loading.sovHubsTotal > 0) {
                val sovHubsPercentage = (loading.sovHubsLoaded.toFloat() / loading.sovHubsTotal).coerceIn(0f, 1f)
                LoadingBox(sovHubsPercentage) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncTypeIcon(
                            typeId = 32458,
                            modifier = Modifier.size(48.dp),
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.small),
                        ) {
                            Text(
                                text = stringResource(Res.string.structures_sovereignty_hubs_tab),
                                style = RiftTheme.typography.bodyPrimary,
                            )

                            Divider(color = Color.White.copy(alpha = 0.15f))

                            val text = if (loading.sovHubsLoaded == loading.sovHubsTotal) {
                                pluralStringResource(Res.plurals.structures_loading_sovereignty_hubs_complete, loading.sovHubsLoaded, loading.sovHubsLoaded)
                            } else {
                                pluralStringResource(Res.plurals.structures_loading_sovereignty_hubs_progress, loading.sovHubsTotal, loading.sovHubsLoaded, loading.sovHubsTotal)
                            }
                            Text(
                                text = text,
                                style = if (loading.sovHubsLoaded == loading.sovHubsTotal) RiftTheme.typography.bodySecondary else RiftTheme.typography.bodyPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}
