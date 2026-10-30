package com.dobrum.evpanosu.ui

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.dobrum.evpanosu.veri.Uygulama
import com.dobrum.evpanosu.veri.UygulamaDeposu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val turkce: Locale = Locale.forLanguageTag("tr-TR")

@Composable
fun AnaEkran(yenileme: Int) {
    val ctx = LocalContext.current
    val depo = remember { UygulamaDeposu(ctx) }
    val kapsam = rememberCoroutineScope()
    var uygulamalar by remember { mutableStateOf<List<Uygulama>>(emptyList()) }

    LaunchedEffect(yenileme) {
        uygulamalar = withContext(Dispatchers.IO) { depo.yukle() }
    }

    // Ana ekrandayken Geri tuşu hiçbir şey yapmaz
    BackHandler {}

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Renk.Zemin)
            .padding(horizontal = 40.dp, vertical = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SaatBasligi()
            UygulamaIzgarasi(
                liste = uygulamalar,
                onAc = { guvenliAc(ctx, it.intent) },
                onBasaAl = { secilen ->
                    depo.basaAl(secilen.paket, uygulamalar)
                    kapsam.launch { uygulamalar = withContext(Dispatchers.IO) { depo.yukle() } }
                }
            )
        }
        Column(
            modifier = Modifier.width(260.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HavaKarti()
            TakvimKarti()
            KameraKarti()
            HizliAyarlar()
        }
    }
}

fun guvenliAc(ctx: Context, intent: Intent) {
    runCatching { ctx.startActivity(intent) }
}

@Composable
private fun SaatBasligi() {
    var simdi by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            simdi = LocalDateTime.now()
            delay(1_000)
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = simdi.format(DateTimeFormatter.ofPattern("HH:mm")),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 36.sp,
            color = Renk.Yazi
        )
        Text(
            text = simdi.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", turkce)),
            fontSize = 13.sp,
            color = Renk.Soluk,
            modifier = Modifier.padding(bottom = 6.dp)
        )
    }
}

@Composable
private fun UygulamaIzgarasi(
    liste: List<Uygulama>,
    onAc: (Uygulama) -> Unit,
    onBasaAl: (Uygulama) -> Unit
) {
    val ilkOdak = remember { FocusRequester() }
    var odakVerildi by remember { mutableStateOf(false) }

    LaunchedEffect(liste.isNotEmpty()) {
        if (liste.isNotEmpty() && !odakVerildi) {
            delay(150)
            runCatching { ilkOdak.requestFocus() }
            odakVerildi = true
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(liste, key = { _, u -> u.paket }) { sira, u ->
            val odakModifier = if (sira == 0) Modifier.focusRequester(ilkOdak) else Modifier
            OdakKutusu(
                onClick = { onAc(u) },
                onLongClick = { onBasaAl(u) },
                modifier = odakModifier.aspectRatio(16f / 9f)
            ) {
                val banner = u.banner
                if (banner != null) {
                    Image(
                        bitmap = banner,
                        contentDescription = u.ad,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        val ikon = u.ikon
                        if (ikon != null) {
                            Image(bitmap = ikon, contentDescription = null, modifier = Modifier.size(26.dp))
                        }
                        Text(
                            text = u.ad,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
