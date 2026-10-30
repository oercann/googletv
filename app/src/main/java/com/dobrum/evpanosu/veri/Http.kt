package com.dobrum.evpanosu.veri

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

/** Basit HTTP yardımcısı; adresteki kullanici:sifre@ bilgisini Basic Auth olarak gönderir. */
object Http {

    private fun ac(adres: String): HttpURLConnection {
        val url = URL(adres)
        val baglanti = url.openConnection() as HttpURLConnection
        baglanti.connectTimeout = 8_000
        baglanti.readTimeout = 10_000
        val kullanici = url.userInfo
        if (!kullanici.isNullOrEmpty()) {
            val kod = Base64.encodeToString(
                URLDecoder.decode(kullanici, "UTF-8").toByteArray(),
                Base64.NO_WRAP
            )
            baglanti.setRequestProperty("Authorization", "Basic $kod")
        }
        return baglanti
    }

    fun metin(adres: String): String {
        val b = ac(adres)
        try {
            return b.inputStream.bufferedReader().use { it.readText() }
        } finally {
            b.disconnect()
        }
    }

    fun resim(adres: String): Bitmap? {
        val b = ac(adres)
        try {
            return b.inputStream.use { BitmapFactory.decodeStream(it) }
        } finally {
            b.disconnect()
        }
    }
}
