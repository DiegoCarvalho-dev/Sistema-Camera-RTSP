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
    private val urlValidator = RTSPUrlValidator()

    interface CameraStatusListener {
        fun onCameraStatusChanged(cameraId: Int, status: String)
        fun onCameraError(cameraId: Int, errorMessage: String)
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
            options.add("--verbose=2")

            libVLC = LibVLC(context, options)
        } catch (e: Exception) {
            Log.e("CameraManager", "Erro ao inicializar VLC: ${e.message}")
            statusListener?.onCameraError(0, "Falha ao inicializar player de vídeo")
        }
    }

    fun connectCamera(cameraId: Int, cameraUrl: String, videoLayout: VLCVideoLayout) {
        val validation = urlValidator.validateUrl(cameraUrl)

        if (!validation.isValid) {
            statusListener?.onCameraError(cameraId, "URL inválida: ${validation.message}")
            return
        }

        val cameraInfo = urlValidator.extractCameraInfo(cameraUrl)
        val formattedUrl = urlValidator.formatUrlWithTimeout(cameraUrl, 15000)

        Log.d("CameraManager", "Conectando câmera $cameraId: ${cameraInfo.host}")

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
                            Log.i("CameraManager", "Câmera $cameraId conectada com sucesso")
                        }
                        MediaPlayer.Event.Stopped -> {
                            statusListener?.onCameraStatusChanged(cameraId, "Desconectado")
                        }
                        MediaPlayer.Event.Error -> {
                            statusListener?.onCameraStatusChanged(cameraId, "Erro de conexão")
                            statusListener?.onCameraError(cameraId, "Falha na conexão com a câmera")
                        }
                        MediaPlayer.Event.Buffering -> {
                            statusListener?.onCameraStatusChanged(cameraId, "Buffering...")
                        }
                    }
                }

                val media = Media(libVLC, formattedUrl)
                setMedia(media)
                play()
            }

            when (cameraId) {
                1 -> mediaPlayer1 = mediaPlayer
                2 -> mediaPlayer2 = mediaPlayer
            }

        } catch (e: Exception) {
            statusListener?.onCameraError(cameraId, "Erro interno: ${e.message}")
            Log.e("CameraManager", "Erro ao conectar câmera $cameraId: ${e.message}")
        }
    }

    fun getCameraConnectionInfo(cameraId: Int): String {
        return when (cameraId) {
            1 -> urlValidator.extractCameraInfo(camera1Url).toString()
            2 -> urlValidator.extractCameraInfo(camera2Url).toString()
            else -> "Câmera não encontrada"
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

    companion object {
        private const val camera1Url = "rtsp://admin:1q2w3e%21QW%40E@192.168.1.108:554/cam/realmonitor?channel=1&subtype=1"
        private const val camera2Url = "rtsp://admin:1q2w3e%21QW%40E@192.168.1.109:554/cam/realmonitor?channel=1&subtype=1"
    }
}