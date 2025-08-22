package com.lbs.background_android_service.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.IBinder
import android.text.format.Formatter
import android.util.Log
import androidx.core.app.NotificationCompat
import com.lbs.background_android_service.lib.HttpServerService
import com.lbs.background_android_service.lib.NanoHTTPD


enum class ServerType(val type: String) {
    NANO("nano"),
    CUSTOM("custom")
}
class BackgroundApi : Service() {
    private val CHANNEL_ID = "ServiceBackgroundAPIChannel"
    private lateinit var httpServerNano: NanoHTTPD
    private var httpServer: HttpServerService? = null
    private val serverType: ServerType = ServerType.NANO
    private val logTag = "BackgroundApi"
    private var httpServerPort = 8080
    private lateinit var httpServerUrl: String
    private val closeIntent = Intent("com.lbs.background_android_service.CLOSE_ACTIVITY")

    private fun log(message: String) {
        Log.d(logTag, "$logTag : $message")
    }

    private fun getApiUrl(): String {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ipAddress = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        return "http://$ipAddress:$httpServerPort"
    }

    override fun onCreate() {
        super.onCreate()
        httpServerUrl = getApiUrl()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getIntExtra("httpServerPort", 8080)?.let { httpServerPort = it }

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("API em execução")
            .setContentText("Seu servidor local está rodando em background")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .build()

        startForeground(1, notification)

        when (serverType) {
            ServerType.NANO -> {
                try {
                    if (!::httpServerNano.isInitialized || !httpServerNano.isAlive) {
                        httpServerNano = NanoHTTPD(context = applicationContext, httpServerPort)
                        httpServerNano.start()

                        log("Servidor NanoHTTPD: Em execução. $httpServerUrl")
                        ServiceStatusHolder.updateState(isRunning = true, url = httpServerUrl)
                        sendBroadcast(closeIntent)
                    } else {
                        log("Servidor NanoHTTPD já em execução. $httpServerUrl")
                        ServiceStatusHolder.updateState(isRunning = true, url = httpServerUrl)
                    }
                } catch (e: Exception) {
                    log("Erro ao iniciar NanoHTTPD: ${e.message}")
                }
            }

            ServerType.CUSTOM -> {
                if (httpServer == null) {
                    httpServer = HttpServerService(port = httpServerPort) { message ->
                        log("Servidor Custom: $message")
                    }.also {
                        it.start()
                    }

                    log("Servidor Custom: Em execução. $httpServerUrl")
                    ServiceStatusHolder.updateState(isRunning = true, url = httpServerUrl)
                    sendBroadcast(closeIntent)
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serverType == ServerType.NANO && ::httpServerNano.isInitialized) {
            httpServerNano.stop()
            log("Servidor NanoHTTPD: Finalizado")
        }
        if (serverType == ServerType.CUSTOM) {
            httpServer?.stop()
            log("Servidor Custom: Finalizado")
        }
        ServiceStatusHolder.updateState(isRunning = false, url = null)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Canal da API em background",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }
}
