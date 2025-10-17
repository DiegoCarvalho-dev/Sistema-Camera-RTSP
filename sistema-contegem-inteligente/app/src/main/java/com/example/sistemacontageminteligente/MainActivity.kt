package com.example.sistemacontageminteligente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.videolan.libvlc.util.VLCVideoLayout

class MainActivity : AppCompatActivity(), CameraManager.CameraStatusListener {

    private lateinit var camera1Layout: VLCVideoLayout
    private lateinit var camera2Layout: VLCVideoLayout
    private lateinit var statusCamera1: TextView
    private lateinit var statusCamera2: TextView

    private lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        setupCameraManager()
        connectCameras()
    }

    private fun setupViews() {
        camera1Layout = findViewById(R.id.camera1Layout)
        camera2Layout = findViewById(R.id.camera2Layout)
        statusCamera1 = findViewById(R.id.statusCamera1)
        statusCamera2 = findViewById(R.id.statusCamera2)

    }

    private fun setupCameraManager() {
        cameraManager = CameraManager(this)
        cameraManager.setStatusListener(this)
        cameraManager.initializeVLC()
    }

    private fun connectCameras() {
        cameraManager.connectCamera(1, CameraConfig.CAMERA_1_URL, camera1Layout)
        cameraManager.connectCamera(2, CameraConfig.CAMERA_2_URL, camera2Layout)
    }

    override fun onCameraStatusChanged(cameraId: Int, status: String) {
        runOnUiThread {
            when (cameraId) {
                1 -> statusCamera1.text = status
                2 -> statusCamera2.text = status
            }
        }
    }

    override fun onCameraError(cameraId: Int, errorMessage: String) {
        runOnUiThread {
            when (cameraId) {
                1 -> statusCamera1.text = "Erro"
                2 -> statusCamera2.text = "Erro"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.release()
    }
}