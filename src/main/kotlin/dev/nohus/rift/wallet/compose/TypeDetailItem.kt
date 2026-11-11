package dev.nohus.rift.wallet.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.AsyncCorporationLogo
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.BorderedToken
import dev.nohus.rift.compose.CharacterDetails
import dev.nohus.rift.compose.ClickableEntity
import dev.nohus.rift.compose.ClickableLocation
import dev.nohus.rift.compose.ClickableType
import dev.nohus.rift.compose.LocationDetails
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.SystemDetails
import dev.nohus.rift.compose.VerticalDivider
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.game.GameUiController
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.wallet_transaction_bought_unit_price
import dev.nohus.rift.generated.resources.wallet_transaction_sold_unit_price
import dev.nohus.rift.generated.resources.wallet_type_corporation_project
import dev.nohus.rift.generated.resources.wallet_type_daily_goal
import dev.nohus.rift.generated.resources.wallet_type_freelance_project
import dev.nohus.rift.generated.resources.wallet_type_quantity
import dev.nohus.rift.generated.resources.wallet_type_unsupported
import dev.nohus.rift.types.TypeDetail
import dev.nohus.rift.utils.formatIsk
import dev.nohus.rift.utils.formatNumber
import dev.nohus.rift.wallet.WalletTransactionItem
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue

/**
 * @param transaction Only used for TypeDetail.Type
 * @param showCents Only used for TypeDetail.Type
 */
@Composable
fun TypeDetailItem(
    type: TypeDetail,
    transaction: WalletTransactionItem? = null,
    showCents: Boolean = false,
    borderColor: Color = RiftTheme.colors.borderGreyLight,
) {
    val rowHeight = 32.dp
    BorderedToken(rowHeight, borderColor) {
        when (type) {
            is TypeDetail.Character -> {
                CharacterDetails(type.character, rowHeight, isAnimated = false)
            }

            is TypeDetail.Corporation -> {
                CharacterDetails(type.corporation, rowHeight)
            }

            is TypeDetail.Alliance -> {
                CharacterDetails(type.alliance, rowHeight)
            }

            is TypeDetail.Faction -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RiftTooltipArea(
                        text = type.name,
                    ) {
                        AsyncCorporationLogo(
                            corporationId = type.id.toInt(),
                            size = 32,
                            modifier = Modifier.size(rowHeight),
                        )
                    }

                    Text(
                        text = type.name,
                        style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }

            is TypeDetail.SolarSystem -> {
                SystemDetails(
                    system = type.system,
                    rowHeight = rowHeight,
                    isShowingSystemDistance = false,
                    isUsingJumpBridges = false,
                )
            }

            is TypeDetail.Station -> {
                LocationDetails(
                    station = type.station,
                    rowHeight = rowHeight,
                )
            }

            is TypeDetail.Structure -> {
                LocationDetails(
                    structure = type.structure,
                    rowHeight = rowHeight,
                )
            }

            is TypeDetail.Type -> {
                ClickableType(type.type) {
                    AsyncTypeIcon(
                        type = type.type,
                        modifier = Modifier.size(rowHeight),
                    )
                }

                ClickableType(type.type) {
                    Column(
                        modifier = Modifier.padding(horizontal = Spacing.small),
                    ) {
                        val quantity = type.count ?: transaction?.quantity
                        val text = if (quantity != null) {
                            stringResource(Res.string.wallet_type_quantity, formatNumber(quantity), type.type.name)
                        } else {
                            type.type.name
                        }
                        Text(
                            text = text,
                            style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold),
                        )
                        val unitPrice = transaction?.unitPrice
                        if (unitPrice != null) {
                            val resource = if (transaction.isBuy) Res.string.wallet_transaction_bought_unit_price else Res.string.wallet_transaction_sold_unit_price
                            val showCents = if (unitPrice.absoluteValue < 10) true else showCents
                            Text(
                                text = stringResource(resource, formatIsk(unitPrice, showCents)),
                                style = RiftTheme.typography.bodySecondary,
                            )
                        }
                    }
                }
            }

            is TypeDetail.CorporationProject -> {
                val gameUiController: GameUiController = remember { koin.get() }
                ClickableEntity(
                    onClick = {
                        gameUiController.pushCorporationProject(type.id, type.name)
                    },
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = Spacing.small),
                    ) {
                        Text(
                            text = type.name,
                            style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = stringResource(Res.string.wallet_type_corporation_project),
                            style = RiftTheme.typography.bodySecondary,
                        )
                    }
                }
            }

            is TypeDetail.FreelanceProject -> {
                val gameUiController: GameUiController = remember { koin.get() }
                ClickableEntity(
                    onClick = {
                        gameUiController.pushFreelanceProject(type.id, type.name)
                    },
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = Spacing.small),
                    ) {
                        Text(
                            text = type.name,
                            style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = stringResource(Res.string.wallet_type_freelance_project),
                            style = RiftTheme.typography.bodySecondary,
                        )
                    }
                }
            }

            is TypeDetail.DailyGoal -> {
                Column(
                    modifier = Modifier.padding(horizontal = Spacing.small),
                ) {
                    Text(
                        text = type.name,
                        style = RiftTheme.typography.bodyHighlighted.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = stringResource(Res.string.wallet_type_daily_goal),
                        style = RiftTheme.typography.bodySecondary,
                    )
                }
            }

            is TypeDetail.Celestial -> {
                ClickableLocation(
                    systemId = type.celestial.solarSystemId,
                    locationId = type.celestial.id.toLong(),
                    locationTypeId = type.celestial.type.id,
                    locationName = type.celestial.name,
                ) {
                    AsyncTypeIcon(
                        type = type.celestial.type,
                        modifier = Modifier.size(rowHeight),
                    )
                }
                VerticalDivider(color = RiftTheme.colors.borderGreyLight, modifier = Modifier.height(rowHeight))
                ClickableLocation(
                    systemId = type.celestial.solarSystemId,
                    locationId = type.celestial.id.toLong(),
                    locationTypeId = type.celestial.type.id,
                    locationName = type.celestial.name,
                ) {
                    Text(
                        text = type.celestial.name,
                        style = RiftTheme.typography.bodyHighlighted,
                        modifier = Modifier.padding(horizontal = Spacing.small),
                    )
                }
            }

            is TypeDetail.Unknown -> {
                Text(
                    stringResource(Res.string.wallet_type_unsupported, type.id.toString()),
                    style = RiftTheme.typography.bodyPrimary.copy(RiftTheme.colors.hotRed),
                    modifier = Modifier.padding(horizontal = Spacing.small),
                )
            }
        }
    }
}
