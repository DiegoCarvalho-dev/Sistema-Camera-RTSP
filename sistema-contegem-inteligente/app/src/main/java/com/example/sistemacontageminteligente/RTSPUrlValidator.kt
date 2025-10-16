package com.exemplo.sistemacontageminteligente

import java.util.regex.Pattern

class RTSPUrlValidator {

    fun validateUrl(url: String): ValidationResult {
        return when {
            url.isBlank() -> ValidationResult(false, "URL não pode estar vazia")
            !isValidRtspUrl(url) -> ValidationResult(false, "URL RTSP inválida")
            !hasValidCredentials(url) -> ValidationResult(false, "Credenciais inválidas na URL")
            else -> ValidationResult(true, "URL válida")
        }
    }

    fun extractCameraInfo(url: String): CameraInfo {
        return try {
            val pattern = Pattern.compile("rtsp://(.*?):(.*?)@(.*?):(\\d+)/(.*)")
            val matcher = pattern.matcher(url)

            if (matcher.find()) {
                val username = matcher.group(1)
                val password = matcher.group(2)
                val host = matcher.group(3)
                val port = matcher.group(4).toInt()
                val path = matcher.group(5)

                CameraInfo(
                    host = host,
                    port = port,
                    username = username,
                    path = path,
                    fullUrl = url
                )
            } else {
                CameraInfo(fullUrl = url)
            }
        } catch (e: Exception) {
            CameraInfo(fullUrl = url)
        }
    }

    fun formatUrlWithTimeout(url: String, timeoutMs: Int = 10000): String {
        return "$url :timeout=$timeoutMs"
    }

    private fun isValidRtspUrl(url: String): Boolean {
        return url.startsWith("rtsp://") && url.contains("@") && url.contains(":")
    }

    private fun hasValidCredentials(url: String): Boolean {
        return try {
            val credentialPattern = Pattern.compile("rtsp://(.*?):(.*?)@")
            val matcher = credentialPattern.matcher(url)
            matcher.find() && matcher.group(1).isNotBlank() && matcher.group(2).isNotBlank()
        } catch (e: Exception) {
            false
        }
    }

    data class ValidationResult(val isValid: Boolean, val message: String)

    data class CameraInfo(
        val host: String = "",
        val port: Int = 554,
        val username: String = "",
        val path: String = "",
        val fullUrl: String = ""
    )
}