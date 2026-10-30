package com.dobrum.evpanosu.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.dobrum.evpanosu.Ayarlar
import com.dobrum.evpanosu.Kamera
import com.dobrum.evpanosu.KameraActivity
import com.dobrum.evpanosu.veri.Etkinlik
import com.dobrum.evpanosu.veri.Hava
import com.dobrum.evpanosu.veri.HavaServisi
import com.dobrum.evpanosu.veri.Http
import com.dobrum.evpanosu.veri.TakvimServisi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
private fun Kart(
    baslik: String? = null,
    modifier: Modifier = Modifier,
    icerik: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Renk.Kart)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (baslik != null) {
            Text(text = baslik, fontSize = 10.sp, color = Renk.Soluk)
        }
        icerik()
    }
}

@Composable
fun HavaKarti() {
    var hava by remember { mutableStateOf<Hava?>(null) }
    LaunchedEffect(Unit) {
        while (true) {
            HavaServisi.getir()?.let { hava = it }
            delay(if (hava == null) 60_000L else 30 * 60_000L)
        }
    }
    Kart {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "Hava · ${Ayarlar.KONUM_ADI}", fontSize = 10.sp, color = Renk.Soluk)
                Text(text = hava?.aciklama ?: "Yükleniyor…", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Renk.Yazi)
            }
            Text(
                text = hava?.let { "${it.sicaklik}°" } ?: "--°",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = Renk.Yazi
            )
        }
    }
}

@Composable
fun TakvimKarti() {
    var etkinlikler by remember { mutableStateOf<List<Etkinlik>?>(emptyList()) }
    LaunchedEffect(Unit) {
        while (true) {
            etkinlikler = TakvimServisi.bugun()
            delay(15 * 60_000L)
        }
    }
    Kart(baslik = "Takvim") {
        val liste = etkinlikler
        when {
            liste == null -> Text(text = "Takvim bağlı değil", fontSize = 12.sp, color = Renk.Soluk)
            liste.isEmpty() -> Text(text = "Bugün etkinlik yok", fontSize = 12.sp, color = Renk.Soluk)
            else -> liste.take(3).forEach { e ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = e.saat,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Renk.Vurgu,
                        modifier = Modifier.width(52.dp)
                    )
                    Text(
                        text = e.baslik,
                        fontSize = 12.sp,
                        color = Renk.Yazi,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun KameraKarti() {
    if (Ayarlar.KAMERALAR.isEmpty()) return
    Kart(baslik = "Kameralar") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Ayarlar.KAMERALAR.take(2).forEach { kamera ->
                KameraKutusu(kamera, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun KameraKutusu(kamera: Kamera, modifier: Modifier) {
    val ctx = LocalContext.current
    var goruntu by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(kamera.goruntuUrl) {
        while (true) {
            val bmp = withContext(Dispatchers.IO) {
                runCatching { Http.resim(kamera.goruntuUrl) }.getOrNull()
            }
            if (bmp != null) goruntu = bmp.asImageBitmap()
            delay(10_000)
        }
    }
    OdakKutusu(
        onClick = { guvenliAc(ctx, KameraActivity.intent(ctx, kamera)) },
        modifier = modifier.aspectRatio(16f / 9f),
        kose = 6.dp
    ) {
        val resim = goruntu
        if (resim != null) {
            Image(
                bitmap = resim,
                contentDescription = kamera.ad,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = kamera.ad,
            fontSize = 9.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color(0x99000000))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun HizliAyarlar() {
    val ctx = LocalContext.current
    val dugmeler = listOf(
        "Ses" to Settings.ACTION_SOUND_SETTINGS,
        "Uyku" to Settings.ACTION_DREAM_SETTINGS,
        "Wi-Fi" to Settings.ACTION_WIFI_SETTINGS,
        "Ayarlar" to Settings.ACTION_SETTINGS
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        dugmeler.forEach { (ad, eylem) ->
            OdakKutusu(
                onClick = { ayarAc(ctx, eylem) },
                modifier = Modifier.weight(1f).height(48.dp),
                kose = 8.dp
            ) {
                Text(
                    text = ad,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

private fun ayarAc(ctx: Context, eylem: String) {
    val yeni = Intent.FLAG_ACTIVITY_NEW_TASK
    runCatching { ctx.startActivity(Intent(eylem).addFlags(yeni)) }
        .onFailure { runCatching { ctx.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(yeni)) } }
}
