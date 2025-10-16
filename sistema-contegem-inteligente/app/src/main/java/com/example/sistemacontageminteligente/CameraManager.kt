package com.exemplo.sistemacontageminteligente

import android.content.Context
import android.util.Log
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

class CameraManager(private val context: Context) {

    private var libVLC: LibVLC? = null
    private var mediaPlayer1: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null

    interface CameraStatusListener {
        fun onCameraStatusChanged(cameraId: Int, status: String)
    }

    private var statusListener: CameraStatusListener? = null

    fun setStatusListener(listener: CameraStatusListener) {
        statusListener = listener
    }

    fun initializeVLC() {
        try {
            val options = ArrayList<String>()
            options.add("--network-caching=300")
            options.add("--rtsp-tcp")
            options.add("--avcodec-hw=any")

            libVLC = LibVLC(context, options)
        } catch (e: Exception) {
            Log.e("CameraManager", "Erro ao inicializar VLC: ${e.message}")
        }
    }

    fun connectCamera(cameraId: Int, cameraUrl: String, videoLayout: VLCVideoLayout) {
        try {
            val mediaPlayer = MediaPlayer(libVLC).apply {
                attachViews(videoLayout, null, false, false)

                setEventListener { event ->
                    when (event.type) {
                        MediaPlayer.Event.Opening -> {
                            statusListener?.onCameraStatusChanged(cameraId, "Conectando...")
                        }
                        MediaPlayer.Event.Playing -> {
                            statusListener?.onCameraStatusChanged(cameraId, "Conectado")
                        }
                        MediaPlayer.Event.Stopped -> {
                            statusListener?.onCameraStatusChanged(cameraId, "Desconectado")
                        }
                        MediaPlayer.Event.Error -> {
                            statusListener?.onCameraStatusChanged(cameraId, "Erro")
                        }
                    }
                }

                val media = Media(libVLC, cameraUrl)
                setMedia(media)
                play()
            }

            when (cameraId) {
                1 -> mediaPlayer1 = mediaPlayer
                2 -> mediaPlayer2 = mediaPlayer
            }

        } catch (e: Exception) {
            statusListener?.onCameraStatusChanged(cameraId, "Erro")
            Log.e("CameraManager", "Erro ao conectar câmera $cameraId: ${e.message}")
        }
    }

    fun reconnectAllCameras(camera1Url: String, camera1Layout: VLCVideoLayout, camera2Url: String, camera2Layout: VLCVideoLayout) {
        mediaPlayer1?.stop()
        mediaPlayer2?.stop()

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            connectCamera(1, camera1Url, camera1Layout)
            connectCamera(2, camera2Url, camera2Layout)
        }, 1000)
    }

    fun release() {
        mediaPlayer1?.detachViews()
        mediaPlayer2?.detachViews()
        mediaPlayer1?.release()
        mediaPlayer2?.release()
        libVLC?.release()
        mediaPlayer1 = null
        mediaPlayer2 = null
        libVLC = null
    }
}