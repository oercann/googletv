package com.dobrum.evpanosu.veri

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

data class Uygulama(
    val paket: String,
    val ad: String,
    val banner: ImageBitmap?,
    val ikon: ImageBitmap?,
    val intent: Intent
)

/** Yüklü TV uygulamalarını listeler; kullanıcının "başa al" sırasını saklar. */
class UygulamaDeposu(private val ctx: Context) {

    private val tercihler = ctx.getSharedPreferences("uygulama_sirasi", Context.MODE_PRIVATE)

    fun yukle(): List<Uygulama> {
        val pm = ctx.packageManager
        val sonuc = LinkedHashMap<String, Uygulama>()

        for (kategori in listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)) {
            val sorgu = Intent(Intent.ACTION_MAIN).addCategory(kategori)
            for (ri in pm.queryIntentActivities(sorgu, 0)) {
                val ai = ri.activityInfo
                val paket = ai.packageName
                if (paket == ctx.packageName || sonuc.containsKey(paket)) continue

                val acmaIntent = Intent(Intent.ACTION_MAIN)
                    .addCategory(kategori)
                    .setClassName(paket, ai.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

                val banner = runCatching { ai.loadBanner(pm) }.getOrNull()
                val ikon = if (banner == null) runCatching { ai.loadIcon(pm) }.getOrNull() else null

                sonuc[paket] = Uygulama(
                    paket = paket,
                    ad = ri.loadLabel(pm).toString(),
                    banner = banner?.let { runCatching { it.toBitmap(320, 180).asImageBitmap() }.getOrNull() },
                    ikon = ikon?.let { runCatching { it.toBitmap(96, 96).asImageBitmap() }.getOrNull() },
                    intent = acmaIntent
                )
            }
        }

        val sira = tercihler.getString("sira", "").orEmpty().split(",").filter { it.isNotBlank() }
        return sonuc.values.sortedWith(
            compareBy<Uygulama>(
                { sira.indexOf(it.paket).let { i -> if (i < 0) Int.MAX_VALUE else i } },
                { it.ad.lowercase() }
            )
        )
    }

    fun basaAl(paket: String, mevcut: List<Uygulama>) {
        val yeni = listOf(paket) + mevcut.map { it.paket }.filter { it != paket }
        tercihler.edit().putString("sira", yeni.joinToString(",")).apply()
    }
}
