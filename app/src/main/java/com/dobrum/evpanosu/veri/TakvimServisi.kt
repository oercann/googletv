package com.dobrum.evpanosu.veri

import com.dobrum.evpanosu.Ayarlar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class Etkinlik(val saat: String, val baslik: String, val zaman: Long)

/**
 * iCal (.ics) adresinden bugünün etkinliklerini çeker.
 * Not: tekrarlayan etkinlikler (RRULE) şimdilik yalnızca ilk tarihinde görünür.
 */
object TakvimServisi {

    suspend fun bugun(): List<Etkinlik>? = withContext(Dispatchers.IO) {
        if (Ayarlar.TAKVIM_ICS_URL.isBlank()) return@withContext null
        runCatching { ayristir(Http.metin(Ayarlar.TAKVIM_ICS_URL)) }.getOrDefault(emptyList())
    }

    private val saatBicimi = DateTimeFormatter.ofPattern("HH:mm")

    fun ayristir(ics: String): List<Etkinlik> {
        val satirlar = ics.replace("\r\n", "\n").replace("\n ", "").replace("\n\t", "").lines()
        val bolge = ZoneId.systemDefault()
        val bugun = LocalDate.now(bolge)
        val liste = mutableListOf<Etkinlik>()

        var icinde = false
        var baslangic: ZonedDateTime? = null
        var tumGun = false
        var baslik = ""

        for (satir in satirlar) {
            when {
                satir.startsWith("BEGIN:VEVENT") -> {
                    icinde = true; baslangic = null; tumGun = false; baslik = ""
                }
                satir.startsWith("END:VEVENT") -> {
                    icinde = false
                    val yerel = baslangic?.withZoneSameInstant(bolge)
                    if (yerel != null && yerel.toLocalDate() == bugun) {
                        liste.add(
                            Etkinlik(
                                saat = if (tumGun) "Tüm gün" else yerel.format(saatBicimi),
                                baslik = baslik.ifBlank { "(Başlıksız)" },
                                zaman = yerel.toInstant().toEpochMilli()
                            )
                        )
                    }
                }
                icinde && satir.startsWith("DTSTART") -> {
                    val sonuc = zamanCoz(satir, bolge)
                    baslangic = sonuc.first
                    tumGun = sonuc.second
                }
                icinde && satir.startsWith("SUMMARY") -> {
                    baslik = satir.substringAfter(":")
                        .replace("\\,", ",").replace("\\;", ";").replace("\\n", " ")
                }
            }
        }
        return liste.sortedBy { it.zaman }
    }

    private fun zamanCoz(satir: String, varsayilan: ZoneId): Pair<ZonedDateTime?, Boolean> {
        val parametreler = satir.substringBefore(":")
        val deger = satir.substringAfter(":").trim()
        return try {
            if (deger.length == 8) {
                Pair(LocalDate.parse(deger, DateTimeFormatter.BASIC_ISO_DATE).atStartOfDay(varsayilan), true)
            } else {
                val ldt = LocalDateTime.parse(
                    deger.removeSuffix("Z"),
                    DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
                )
                val bolge: ZoneId = when {
                    deger.endsWith("Z") -> ZoneOffset.UTC
                    parametreler.contains("TZID=") ->
                        ZoneId.of(parametreler.substringAfter("TZID=").substringBefore(";"))
                    else -> varsayilan
                }
                Pair(ldt.atZone(bolge), false)
            }
        } catch (e: Exception) {
            Pair(null, false)
        }
    }
}
