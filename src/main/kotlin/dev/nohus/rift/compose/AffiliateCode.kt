package dev.nohus.rift.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nohus.rift.clipboard.Clipboard
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.about_affiliate_code_click_copy_discount_code_text
import dev.nohus.rift.generated.resources.about_affiliate_code_discount_code_text
import dev.nohus.rift.generated.resources.about_affiliate_code_plex_omega_more_text
import dev.nohus.rift.utils.openBrowser
import dev.nohus.rift.utils.toURIOrNull
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AffiliateCode() {
    val code = "RIFTINTEL"
    RiftTooltipArea(
        text = stringResource(Res.string.about_affiliate_code_click_copy_discount_code_text),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .onClick { Clipboard.copy(code) }
                .hoverBackground()
                .border(1.dp, RiftTheme.colors.borderGreyLight)
                .fillMaxWidth()
                .padding(Spacing.medium),
        ) {
            Text(
                text = stringResource(Res.string.about_affiliate_code_discount_code_text),
                style = RiftTheme.typography.headlineHighlighted.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = code,
                style = RiftTheme.typography.headerPrimary,
            )
            Row(
                modifier = Modifier.padding(top = Spacing.small),
            ) {
                LinkText(
                    text = stringResource(Res.string.about_affiliate_code_plex_omega_more_text),
                    onClick = {
                        Clipboard.copy(code)
                        "https://store.markeedragon.com/affiliate.php?id=1087&redirect=index.php?cat=4".toURIOrNull()?.openBrowser()
                    },
                )
            }
        }
    }
}
