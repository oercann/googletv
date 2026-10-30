package com.dobrum.evpanosu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableIntStateOf
import com.dobrum.evpanosu.ui.AnaEkran
import com.dobrum.evpanosu.ui.EvPanosuTema

class MainActivity : ComponentActivity() {

    // Her geri dönüşte uygulama listesi yenilenir (yeni yüklenen/kaldırılan uygulamalar için)
    private val yenileme = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EvPanosuTema {
                AnaEkran(yenileme = yenileme.intValue)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        yenileme.intValue++
    }
}
