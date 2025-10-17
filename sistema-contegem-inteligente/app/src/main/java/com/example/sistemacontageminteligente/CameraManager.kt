package com.example.sistemacontageminteligente

import android.content.Context
import android.os.Handler
import android.os.Looper
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
    private val reconnectHandler = Handler(Looper.getMainLooper())

    private var isReconnectingCamera1 = false
    private var isReconnectingCamera2 = false

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
            libVLC = LibVLC(context, arrayListOf("--rtsp-tcp"))
        } catch (e: Exception) {
            Log.e("CameraManager", "Erro ao inicializar VLC: ${e.message}")
            statusListener?.onCameraError(0, "Falha ao inicializar player de vídeo")
        }
    }

    fun connectCamera(cameraId: Int, cameraUrl: String, videoLayout: VLCVideoLayout) {
        if (isCameraReconnecting(cameraId)) {
            Log.d("CameraManager", "Câmera $cameraId já está em processo de reconexão.")
            return
        }

        val validation = urlValidator.validateUrl(cameraUrl)
        if (!validation.isValid) {
            statusListener?.onCameraError(cameraId, "URL inválida: ${validation.message}")
            return
        }

        Log.d("CameraManager", "Conectando câmera $cameraId")
        val formattedUrl = urlValidator.formatUrlWithTimeout(cameraUrl, CameraConfig.NETWORK_TIMEOUT)

        try {
            val mediaPlayer = MediaPlayer(libVLC).apply {
                attachViews(videoLayout, null, false, false)
                setEventListener { event -> handlePlayerEvent(event, cameraId, cameraUrl, videoLayout) }

                val media = Media(libVLC, formattedUrl)
                media.setHWDecoderEnabled(true, false)
                setMedia(media)
                play()
            }

            if (cameraId == 1) mediaPlayer1 = mediaPlayer else mediaPlayer2 = mediaPlayer

        } catch (e: Exception) {
            statusListener?.onCameraError(cameraId, "Erro interno: ${e.message}")
            Log.e("CameraManager", "Erro ao conectar câmera $cameraId: ${e.message}")
            scheduleAutoReconnect(cameraId, cameraUrl, videoLayout)
        }
    }

    private fun handlePlayerEvent(event: MediaPlayer.Event, cameraId: Int, cameraUrl: String, videoLayout: VLCVideoLayout) {
        when (event.type) {
            MediaPlayer.Event.Opening -> statusListener?.onCameraStatusChanged(cameraId, "Conectando...")
            MediaPlayer.Event.Playing -> {
                statusListener?.onCameraStatusChanged(cameraId, "Conectado")
                Log.i("CameraManager", "Câmera $cameraId conectada com sucesso")
                setReconnectingFlag(cameraId, false)
            }
            MediaPlayer.Event.Stopped, MediaPlayer.Event.EncounteredError -> {
                val status = if (event.type == MediaPlayer.Event.Stopped) "Desconectado" else "Erro de conexão"
                statusListener?.onCameraStatusChanged(cameraId, status)
                if (event.type == MediaPlayer.Event.EncounteredError) {
                    statusListener?.onCameraError(cameraId, "Falha na conexão com a câmera")
                }
                scheduleAutoReconnect(cameraId, cameraUrl, videoLayout)
            }
            MediaPlayer.Event.Buffering -> statusListener?.onCameraStatusChanged(cameraId, "Buffering...")
        }
    }

    private fun scheduleAutoReconnect(cameraId: Int, cameraUrl: String, videoLayout: VLCVideoLayout) {
        if (isCameraReconnecting(cameraId)) return

        setReconnectingFlag(cameraId, true)
        Log.d("CameraManager", "Agendando reconexão para câmera $cameraId em ${CameraConfig.RECONNECT_DELAY}ms")

        reconnectHandler.postDelayed({
            Log.d("CameraManager", "Executando reconexão da câmera $cameraId")
            statusListener?.onCameraStatusChanged(cameraId, "Reconectando...")
            connectCamera(cameraId, cameraUrl, videoLayout)
        }, CameraConfig.RECONNECT_DELAY.toLong())
    }

    fun release() {
        mediaPlayer1?.release()
        mediaPlayer2?.release()
        libVLC?.release()
        reconnectHandler.removeCallbacksAndMessages(null)
        mediaPlayer1 = null
        mediaPlayer2 = null
        libVLC = null
        isReconnectingCamera1 = false
        isReconnectingCamera2 = false
    }

    private fun isCameraReconnecting(cameraId: Int) = if (cameraId == 1) isReconnectingCamera1 else isReconnectingCamera2

    private fun setReconnectingFlag(cameraId: Int, isReconnecting: Boolean) {
        if (cameraId == 1) isReconnectingCamera1 = isReconnecting else isReconnectingCamera2 = isReconnecting
    }
}