package com.dobrum.evpanosu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.PlayerView

/** Kamerayı tam ekran oynatır (RTSP veya HLS). Geri tuşu panoya döner. */
@OptIn(UnstableApi::class)
class KameraActivity : ComponentActivity() {

    private var oynatici: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adres = intent.getStringExtra(EK_ADRES)
        if (adres.isNullOrBlank()) {
            finish()
            return
        }

        val p = ExoPlayer.Builder(this).build()
        oynatici = p
        val oge = MediaItem.fromUri(adres)
        if (adres.startsWith("rtsp", ignoreCase = true)) {
            p.setMediaSource(
                RtspMediaSource.Factory()
                    .setForceUseRtpTcp(true)
                    .createMediaSource(oge)
            )
        } else {
            p.setMediaItem(oge)
        }
        p.playWhenReady = true
        p.prepare()

        setContentView(
            PlayerView(this).apply {
                player = p
                useController = false
                keepScreenOn = true
            }
        )
    }

    override fun onDestroy() {
        oynatici?.release()
        oynatici = null
        super.onDestroy()
    }

    companion object {
        private const val EK_ADRES = "adres"

        fun intent(ctx: Context, kamera: Kamera): Intent =
            Intent(ctx, KameraActivity::class.java).putExtra(EK_ADRES, kamera.yayinUrl)
    }
}
