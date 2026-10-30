package com.dobrum.evpanosu.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

object Renk {
    val Zemin = Color(0xFF111418)
    val Kart = Color(0xFF1B2027)
    val KartOdak = Color(0xFF2A313B)
    val Yazi = Color(0xFFECEEF1)
    val Soluk = Color(0xFFA9AFB8)
    val Vurgu = Color(0xFFF2A541)
}

@Composable
fun EvPanosuTema(icerik: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Renk.Vurgu,
            background = Renk.Zemin,
            surface = Renk.Kart,
            onSurface = Renk.Yazi,
            onBackground = Renk.Yazi
        ),
        content = icerik
    )
}
