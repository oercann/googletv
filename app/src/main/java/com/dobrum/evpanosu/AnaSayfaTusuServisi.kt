package com.dobrum.evpanosu

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent

/**
 * Google TV, ana ekranı başka bir uygulamaya bırakmaya izin vermiyor.
 * Bu servis, Google TV'nin kendi ana ekranı açıldığı anda Ev Panosu'nu öne getirir.
 * Ayarlar > Sistem > Erişilebilirlik > Ev Panosu'ndan açılır.
 * Ekran içeriğini okumaz, yalnızca hangi pencerenin açıldığına bakar.
 */
class AnaSayfaTusuServisi : AccessibilityService() {

    private var sonAcilis = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val sinif = event.className?.toString() ?: return

        // Ayar panelleri gibi aynı paketteki başka pencereleri atla; yalnızca ana ekran etkinliği
        val paket = event.packageName?.toString() ?: return
        val haric = listOf("Setting", "Panel", "Dialog", "Profile", "Notification", "Search", "Input")
        val anaEkranMi = sinif.endsWith("MainActivity") ||
            sinif.contains("HomeActivity") ||
            sinif.contains(".home.", ignoreCase = true) ||
            (sinif.startsWith(paket) && haric.none { sinif.contains(it, ignoreCase = true) })
        if (!anaEkranMi) return

        val simdi = SystemClock.elapsedRealtime()
        if (simdi - sonAcilis < 800) return
        sonAcilis = simdi

        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    override fun onInterrupt() {}
}
