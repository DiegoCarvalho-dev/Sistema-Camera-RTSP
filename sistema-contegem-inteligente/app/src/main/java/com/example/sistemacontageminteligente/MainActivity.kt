package com.seunome.smartcounting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

class MainActivity : AppCompatActivity() {

    private lateinit var camera1Layout: VLCVideoLayout
    private lateinit var camera2Layout: VLCVideoLayout
    private lateinit var btnReconnect: Button
    private lateinit var statusCamera1: TextView
    private lateinit var statusCamera2: TextView

    private var libVLC: LibVLC? = null
    private var mediaPlayer1: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null

    private val camera1Url = "rtsp://admin:1q2w3e%21QW%40E@192.168.1.108:554/cam/realmonitor?channel=1&subtype=1"
    private val camera2Url = "rtsp://admin:1q2w3e%21QW%40E@192.168.1.109:554/cam/realmonitor?channel=1&subtype=1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectViews()
        setupVLC()
        connectCameras()
    }

    private fun connectViews() {
        camera1Layout = findViewById(R.id.camera1Layout)
        camera2Layout = findViewById(R.id.camera2Layout)
        btnReconnect = findViewById(R.id.btnReconnect)
        statusCamera1 = findViewById(R.id.statusCamera1)
        statusCamera2 = findViewById(R.id.statusCamera2)

        btnReconnect.setOnClickListener {
            reconnectCameras()
        }
    }

    private fun setupVLC() {
        try {
            val options = ArrayList<String>()
            options.add("--network-caching=300")
            options.add("--rtsp-tcp")

            libVLC = LibVLC(this, options)
        } catch (e: Exception) {
            showError("Erro ao configurar player de vídeo")
        }
    }

    private fun connectCameras() {
        connectCamera1()
        connectCamera2()
    }

    private fun connectCamera1() {
        try {
            mediaPlayer1 = MediaPlayer(libVLC).apply {
                attachViews(camera1Layout, null, false, false)

                setEventListener { event ->
                    when (event.type) {
                        MediaPlayer.Event.Opening -> {
                            runOnUiThread { statusCamera1.text = "Conectando..." }
                        }
                        MediaPlayer.Event.Playing -> {
                            runOnUiThread { statusCamera1.text = "Conectado" }
                        }
                        MediaPlayer.Event.Stopped -> {
                            runOnUiThread { statusCamera1.text = "Desconectado" }
                        }
                        MediaPlayer.Event.Error -> {
                            runOnUiThread { statusCamera1.text = "Erro" }
                        }
                    }
                }

                val media = Media(libVLC, camera1Url)
                setMedia(media)
                play()
            }
        } catch (e: Exception) {
            runOnUiThread { statusCamera1.text = "Erro" }
        }
    }

    private fun connectCamera2() {
        try {
            mediaPlayer2 = MediaPlayer(libVLC).apply {
                attachViews(camera2Layout, null, false, false)

                setEventListener { event ->
                    when (event.type) {
                        MediaPlayer.Event.Opening -> {
                            runOnUiThread { statusCamera2.text = "Conectando..." }
                        }
                        MediaPlayer.Event.Playing -> {
                            runOnUiThread { statusCamera2.text = "Conectado" }
                        }
                        MediaPlayer.Event.Stopped -> {
                            runOnUiThread { statusCamera2.text = "Desconectado" }
                        }
                        MediaPlayer.Event.Error -> {
                            runOnUiThread { statusCamera2.text = "Erro" }
                        }
                    }
                }

                val media = Media(libVLC, camera2Url)
                setMedia(media)
                play()
            }
        } catch (e: Exception) {
            runOnUiThread { statusCamera2.text = "Erro" }
        }
    }

    private fun reconnectCameras() {
        mediaPlayer1?.stop()
        mediaPlayer2?.stop()

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            connectCameras()
        }, 1000)
    }

    private fun showError(message: String) {
        android.util.Log.e("RTSP_APP", message)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer1?.detachViews()
        mediaPlayer2?.detachViews()
        mediaPlayer1?.release()
        mediaPlayer2?.release()
        libVLC?.release()
    }
}