package com.dobrum.evpanosu

/**
 * Ev Panosu ayarları — değiştirip yeniden derleyin.
 */
object Ayarlar {

    // Hava durumu (Open-Meteo, anahtar gerektirmez)
    const val KONUM_ADI = "Caldwell"
    const val ENLEM = 40.8398
    const val BOYLAM = -74.2765
    const val SICAKLIK_BIRIMI = "celsius" // veya "fahrenheit"

    // Google Takvim > Ayarlar > (takvim) > "iCal biçimindeki gizli adres"
    // Boş bırakılırsa takvim kartında "Takvim bağlı değil" yazar.
    const val TAKVIM_ICS_URL = ""

    // Kameralar: küçük önizleme için JPEG anlık görüntü adresi,
    // tıklanınca tam ekran açılacak RTSP veya HLS yayın adresi.
    val KAMERALAR = listOf(
        Kamera(
            ad = "Kapı önü",
            goruntuUrl = "http://kullanici:sifre@192.168.1.50/snapshot.jpg",
            yayinUrl = "rtsp://kullanici:sifre@192.168.1.50:554/stream1"
        ),
        Kamera(
            ad = "Bahçe",
            goruntuUrl = "http://kullanici:sifre@192.168.1.51/snapshot.jpg",
            yayinUrl = "rtsp://kullanici:sifre@192.168.1.51:554/stream1"
        )
    )
}

data class Kamera(val ad: String, val goruntuUrl: String, val yayinUrl: String)
