package com.exemplo.sistemacontageminteligente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.videolan.libvlc.util.VLCVideoLayout

class MainActivity : AppCompatActivity(), CameraManager.CameraStatusListener {

    private lateinit var camera1Layout: VLCVideoLayout
    private lateinit var camera2Layout: VLCVideoLayout
    private lateinit var btnReconnect: Button
    private lateinit var statusCamera1: TextView
    private lateinit var statusCamera2: TextView

    private lateinit var cameraManager: CameraManager

    private val camera1Url = "rtsp://admin:1q2w3e%21QW%40E@192.168.1.108:554/cam/realmonitor?channel=1&subtype=1"
    private val camera2Url = "rtsp://admin:1q2w3e%21QW%40E@192.168.1.109:554/cam/realmonitor?channel=1&subtype=1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectViews()
        setupCameraManager()
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

    private fun setupCameraManager() {
        cameraManager = CameraManager(this)
        cameraManager.setStatusListener(this)
        cameraManager.initializeVLC()
    }

    private fun connectCameras() {
        cameraManager.connectCamera(1, camera1Url, camera1Layout)
        cameraManager.connectCamera(2, camera2Url, camera2Layout)
    }

    override fun onCameraStatusChanged(cameraId: Int, status: String) {
        runOnUiThread {
            when (cameraId) {
                1 -> statusCamera1.text = status
                2 -> statusCamera2.text = status
            }
        }
    }

    private fun reconnectCameras() {
        cameraManager.reconnectAllCameras(camera1Url, camera1Layout, camera2Url, camera2Layout)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.release()
    }
}