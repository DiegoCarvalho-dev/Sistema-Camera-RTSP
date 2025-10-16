package com.exemplo.sistemacontageminteligente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.videolan.libvlc.util.VLCVideoLayout

class MainActivity : AppCompatActivity() {

    private lateinit var camera1Layout: VLCVideoLayout
    private lateinit var camera2Layout: VLCVideoLayout
    private lateinit var btnReconnect: Button
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
        btnReconnect = findViewById(R.id.btnReconnect)
        statusCamera1 = findViewById(R.id.statusCamera1)
        statusCamera2 = findViewById(R.id.statusCamera2)

        btnReconnect.setOnClickListener {
            reconnectCameras()
        }
    }

    private fun setupCameraManager() {
        cameraManager = CameraManager(this)
        cameraManager.setStatusListener(object : CameraManager.CameraStatusListener {
            override fun onCameraStatusChanged(cameraId: Int, status: String) {
                runOnUiThread {
                    if (cameraId == 1) statusCamera1.text = status
                    if (cameraId == 2) statusCamera2.text = status
                }
            }

            override fun onCameraError(cameraId: Int, errorMessage: String) {
                runOnUiThread {
                    if (cameraId == 1) statusCamera1.text = "Erro"
                    if (cameraId == 2) statusCamera2.text = "Erro"
                }
            }
        })
        cameraManager.initializeVLC()
    }

    private fun connectCameras() {
        cameraManager.connectCamera(1, CameraConfig.CAMERA_1_URL, camera1Layout)
        cameraManager.connectCamera(2, CameraConfig.CAMERA_2_URL, camera2Layout)
    }

    private fun reconnectCameras() {
        cameraManager.reconnectAllCameras(
            CameraConfig.CAMERA_1_URL,
            camera1Layout,
            CameraConfig.CAMERA_2_URL,
            camera2Layout
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.release()
    }
}