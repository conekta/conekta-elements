package io.conekta.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.conekta.compose.generated.resources.Res
import io.conekta.compose.generated.resources.checkout_footer_help
import io.conekta.compose.generated.resources.checkout_footer_privacy
import io.conekta.compose.theme.ConektaColors
import io.conekta.compose.theme.LocalConektaFontFamily
import io.conekta.compose.utils.colorFromHex
import io.conekta.elements.resources.CDNResources
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckoutFooter(
    modifier: Modifier = Modifier,
    selectedLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val fontFamily = LocalConektaFontFamily.current
    val dropdownMenuColor = colorFromHex(CDNResources.Colors.CHECKOUT_INK)
    val dropdownMenuContentColor = colorFromHex(CDNResources.Colors.WHITE)
    var languageMenuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(colorFromHex(CDNResources.Colors.CHECKOUT_BACKGROUND))
                .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        FooterLink(
            text = stringResource(Res.string.checkout_footer_privacy),
            onClick = { uriHandler.openUri(CDNResources.Links.PRIVACY) },
            fontFamily = fontFamily,
        )
        FooterSeparator()
        FooterLink(
            text = stringResource(Res.string.checkout_footer_help),
            onClick = { uriHandler.openUri(CDNResources.Links.HELP) },
            fontFamily = fontFamily,
        )
        FooterSeparator()
        Box {
            Row(
                modifier = Modifier.clickable { languageMenuExpanded = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Language,
                    contentDescription = null,
                    tint = ConektaColors.Neutral8,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = selectedLanguageTag.uppercase(),
                    style = TextStyle(fontFamily = fontFamily, fontSize = 14.sp, color = ConektaColors.Neutral8),
                )
                Icon(
                    imageVector = Icons.Outlined.UnfoldMore,
                    contentDescription = null,
                    tint = ConektaColors.Neutral8,
                    modifier = Modifier.size(16.dp),
                )
            }

            DropdownMenu(
                expanded = languageMenuExpanded,
                onDismissRequest = { languageMenuExpanded = false },
                containerColor = dropdownMenuColor,
            ) {
                listOf("es" to "ES", "en" to "EN").forEach { (tag, label) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                style =
                                    TextStyle(
                                        fontFamily = fontFamily,
                                        fontSize = 14.sp,
                                        color = dropdownMenuContentColor,
                                    ),
                            )
                        },
                        onClick = {
                            onLanguageSelected(tag)
                            languageMenuExpanded = false
                        },
                        leadingIcon = {
                            if (selectedLanguageTag == tag) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = dropdownMenuContentColor,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun FooterLink(
    text: String,
    onClick: () -> Unit,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
) {
    Text(
        modifier = Modifier.clickable(onClick = onClick),
        text = text,
        style =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.sp,
                color = ConektaColors.Neutral8,
                textDecoration = TextDecoration.Underline,
            ),
    )
}

@Composable
private fun FooterSeparator() {
    Spacer(modifier = Modifier.width(16.dp))
    Box(modifier = Modifier.height(18.dp).width(1.dp).background(colorFromHex(CDNResources.Colors.CHECKOUT_BORDER)))
    Spacer(modifier = Modifier.width(16.dp))
}
