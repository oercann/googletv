package com.dobrum.evpanosu.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface

/** Kumandayla seçilince büyüyen ve turuncu çerçeve alan kutu. */
@Composable
fun OdakKutusu(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    kose: Dp = 10.dp,
    icerik: @Composable BoxScope.() -> Unit
) {
    val sekil = RoundedCornerShape(kose)
    Surface(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier,
        shape = ClickableSurfaceDefaults.shape(shape = sekil),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Renk.Kart,
            contentColor = Renk.Yazi,
            focusedContainerColor = Renk.KartOdak,
            focusedContentColor = Renk.Yazi,
            pressedContainerColor = Renk.KartOdak,
            pressedContentColor = Renk.Yazi
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(border = BorderStroke(2.dp, Renk.Vurgu), shape = sekil)
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.06f),
        content = icerik
    )
}
