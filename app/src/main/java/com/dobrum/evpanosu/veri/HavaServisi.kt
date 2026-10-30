package com.dobrum.evpanosu.veri

import com.dobrum.evpanosu.Ayarlar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.roundToInt

data class Hava(val sicaklik: Int, val aciklama: String)

object HavaServisi {

    suspend fun getir(): Hava? = withContext(Dispatchers.IO) {
        runCatching {
            val adres = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=${Ayarlar.ENLEM}&longitude=${Ayarlar.BOYLAM}" +
                "&current=temperature_2m,weather_code" +
                "&temperature_unit=${Ayarlar.SICAKLIK_BIRIMI}&timezone=auto"
            val simdi = JSONObject(Http.metin(adres)).getJSONObject("current")
            Hava(
                sicaklik = simdi.getDouble("temperature_2m").roundToInt(),
                aciklama = aciklama(simdi.getInt("weather_code"))
            )
        }.getOrNull()
    }

    private fun aciklama(kod: Int): String = when (kod) {
        0 -> "Açık"
        1, 2 -> "Parçalı bulutlu"
        3 -> "Kapalı"
        45, 48 -> "Sisli"
        in 51..57 -> "Çisenti"
        in 61..67 -> "Yağmurlu"
        in 71..77 -> "Karlı"
        in 80..82 -> "Sağanak"
        85, 86 -> "Kar sağanağı"
        in 95..99 -> "Gök gürültülü"
        else -> "—"
    }
}
