package dev.nohus.rift.wallet.compose

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.AsyncCorporationLogo
import dev.nohus.rift.compose.LoadingBox
import dev.nohus.rift.compose.LoadingSpinnerAmbient
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.VerticalGrid
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.dynamicportraits.DynamicCharacterPortraitParallax
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.wallet_loading_balances
import dev.nohus.rift.generated.resources.wallet_loading_characters_loaded
import dev.nohus.rift.generated.resources.wallet_loading_characters_progress
import dev.nohus.rift.generated.resources.wallet_loading_division_names
import dev.nohus.rift.generated.resources.wallet_loading_groups_loaded
import dev.nohus.rift.generated.resources.wallet_loading_groups_progress
import dev.nohus.rift.generated.resources.wallet_loading_journal_loaded
import dev.nohus.rift.generated.resources.wallet_loading_journal_progress
import dev.nohus.rift.generated.resources.wallet_loading_market_loaded
import dev.nohus.rift.generated.resources.wallet_loading_market_progress
import dev.nohus.rift.generated.resources.wallet_loading_references
import dev.nohus.rift.generated.resources.wallet_loading_structures_loaded
import dev.nohus.rift.generated.resources.wallet_loading_structures_progress
import dev.nohus.rift.generated.resources.wallet_loading_systems_progress
import dev.nohus.rift.generated.resources.wallet_loading_transaction_details
import dev.nohus.rift.generated.resources.wallet_loading_transactions
import dev.nohus.rift.generated.resources.wallet_loading_types_progress
import dev.nohus.rift.generated.resources.wallet_processing_transactions
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.utils.formatNumber
import dev.nohus.rift.utils.withColor
import dev.nohus.rift.wallet.WalletRepository
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WalletLoadingProgress(
    loading: WalletRepository.LoadingState,
    stage: WalletRepository.LoadingStage,
) {
    ScrollbarColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(Spacing.large),
    ) {
        LoadingSpinnerAmbient()
        Spacer(Modifier.height(Spacing.medium))
        when (stage) {
            WalletRepository.LoadingStage.LoadingJournal -> {
                Text(
                    text = stringResource(Res.string.wallet_loading_transactions),
                    style = RiftTheme.typography.headlinePrimary,
                )
            }

            WalletRepository.LoadingStage.LoadingDatabase -> {
                Text(
                    text = stringResource(Res.string.wallet_processing_transactions),
                    style = RiftTheme.typography.headlinePrimary,
                )
            }

            WalletRepository.LoadingStage.LoadingTypeDetails -> {
                Text(
                    text = stringResource(Res.string.wallet_loading_transaction_details),
                    style = RiftTheme.typography.headlinePrimary,
                )
            }

            WalletRepository.LoadingStage.LoadingDivisionNames -> {
                Text(
                    text = stringResource(Res.string.wallet_loading_division_names),
                    style = RiftTheme.typography.headlinePrimary,
                )
            }

            WalletRepository.LoadingStage.LoadingBalances -> {
                Text(
                    text = stringResource(Res.string.wallet_loading_balances),
                    style = RiftTheme.typography.headlinePrimary,
                )
            }
        }

        Spacer(Modifier.height(Spacing.large))

        AnimatedContent(stage) { stage ->
            when (stage) {
                WalletRepository.LoadingStage.LoadingJournal,
                WalletRepository.LoadingStage.LoadingDatabase,
                -> {
                    VerticalGrid(
                        minColumnWidth = 300.dp,
                        horizontalSpacing = Spacing.medium,
                        verticalSpacing = Spacing.medium,
                    ) {
                        loading.characters.forEach { character ->
                            LoadingBox(character.journalItemsProgress) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    DynamicCharacterPortraitParallax(
                                        characterId = character.characterId,
                                        size = 48.dp,
                                        enterTimestamp = null,
                                        pointerInteractionStateHolder = null,
                                    )
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                    ) {
                                        Text(
                                            text = character.name,
                                            style = RiftTheme.typography.bodyPrimary,
                                        )
                                        Divider(color = Color.White.copy(alpha = 0.15f))
                                        when (stage) {
                                            WalletRepository.LoadingStage.LoadingJournal, WalletRepository.LoadingStage.LoadingDatabase -> {
                                                val journalText = pluralStringResource(
                                                    if (character.isJournalLoaded) Res.plurals.wallet_loading_journal_loaded else Res.plurals.wallet_loading_journal_progress,
                                                    character.loadedJournalItems,
                                                    formatNumber(character.loadedJournalItems),
                                                )
                                                val marketText = pluralStringResource(
                                                    if (character.isJournalLoaded) Res.plurals.wallet_loading_market_loaded else Res.plurals.wallet_loading_market_progress,
                                                    character.loadedTransactions,
                                                    formatNumber(character.loadedTransactions),
                                                )
                                                val text = if (character.isJournalLoaded) {
                                                    buildAnnotatedString {
                                                        withColor(RiftTheme.colors.textSecondary) {
                                                            appendLine(journalText)
                                                            appendLine(marketText)
                                                        }
                                                    }
                                                } else {
                                                    buildAnnotatedString {
                                                        appendLine(journalText)
                                                        appendLine(marketText)
                                                    }
                                                }.trim() as AnnotatedString
                                                Text(
                                                    text = text,
                                                    style = RiftTheme.typography.bodyPrimary,
                                                )
                                            }
                                            WalletRepository.LoadingStage.LoadingTypeDetails -> {}
                                            WalletRepository.LoadingStage.LoadingDivisionNames -> {}
                                            WalletRepository.LoadingStage.LoadingBalances -> {}
                                        }
                                    }
                                }
                            }
                        }
                        loading.corporationDivisions.forEach { corpDivision ->
                            LoadingBox(corpDivision.journalItemsProgress) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AsyncCorporationLogo(
                                        corporationId = corpDivision.corporationId,
                                        size = 64,
                                        modifier = Modifier.size(48.dp),
                                    )
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                    ) {
                                        Text(
                                            text = corpDivision.name.resolve(),
                                            style = RiftTheme.typography.bodyPrimary,
                                        )
                                        Divider(color = Color.White.copy(alpha = 0.15f))
                                        when (stage) {
                                            WalletRepository.LoadingStage.LoadingJournal, WalletRepository.LoadingStage.LoadingDatabase -> {
                                                val journalText = pluralStringResource(
                                                    if (corpDivision.isJournalLoaded) Res.plurals.wallet_loading_journal_loaded else Res.plurals.wallet_loading_journal_progress,
                                                    corpDivision.loadedJournalItems,
                                                    formatNumber(corpDivision.loadedJournalItems),
                                                )
                                                val marketText = pluralStringResource(
                                                    if (corpDivision.isJournalLoaded) Res.plurals.wallet_loading_market_loaded else Res.plurals.wallet_loading_market_progress,
                                                    corpDivision.loadedTransactions,
                                                    formatNumber(corpDivision.loadedTransactions),
                                                )
                                                val text = if (corpDivision.isJournalLoaded) {
                                                    buildAnnotatedString {
                                                        withColor(RiftTheme.colors.textSecondary) {
                                                            appendLine(journalText)
                                                            appendLine(marketText)
                                                        }
                                                    }
                                                } else {
                                                    buildAnnotatedString {
                                                        appendLine(journalText)
                                                        appendLine(marketText)
                                                    }
                                                }.trim() as AnnotatedString
                                                Text(
                                                    text = text,
                                                    style = RiftTheme.typography.bodyPrimary,
                                                )
                                            }
                                            WalletRepository.LoadingStage.LoadingTypeDetails -> {}
                                            WalletRepository.LoadingStage.LoadingDivisionNames -> {}
                                            WalletRepository.LoadingStage.LoadingBalances -> {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                WalletRepository.LoadingStage.LoadingTypeDetails -> {
                    Text(
                        text = buildAnnotatedString {
                            appendLine(pluralStringResource(Res.plurals.wallet_loading_references, loading.totalJournalItems, formatNumber(loading.totalJournalItems)))
                            appendLine(
                                pluralStringResource(
                                    if (loading.typeDetails.isStructuresLoaded) Res.plurals.wallet_loading_structures_loaded else Res.plurals.wallet_loading_structures_progress,
                                    loading.typeDetails.structureIds,
                                    loading.typeDetails.structureIds.toString(),
                                ),
                            )
                            appendLine(
                                pluralStringResource(
                                    if (loading.typeDetails.isCharactersLoaded) Res.plurals.wallet_loading_characters_loaded else Res.plurals.wallet_loading_characters_progress,
                                    loading.typeDetails.characterIds,
                                    loading.typeDetails.characterIds.toString(),
                                ),
                            )
                            appendLine(
                                pluralStringResource(
                                    if (loading.typeDetails.isGroupsLoaded) Res.plurals.wallet_loading_groups_loaded else Res.plurals.wallet_loading_groups_progress,
                                    loading.typeDetails.groupIds,
                                    loading.typeDetails.groupIds.toString(),
                                ),
                            )
                            appendLine(pluralStringResource(Res.plurals.wallet_loading_systems_progress, loading.typeDetails.systemIds, loading.typeDetails.systemIds.toString()))
                            appendLine(pluralStringResource(Res.plurals.wallet_loading_types_progress, loading.typeDetails.typeIds, loading.typeDetails.typeIds.toString()))
                        }.trim() as AnnotatedString,
                        style = RiftTheme.typography.headerSecondary,
                    )
                }

                WalletRepository.LoadingStage.LoadingDivisionNames -> {}
                WalletRepository.LoadingStage.LoadingBalances -> {}
            }
        }
    }
}
