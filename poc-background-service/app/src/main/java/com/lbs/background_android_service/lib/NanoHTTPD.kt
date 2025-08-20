package com.lbs.background_android_service.lib
import android.content.Context
import com.lbs.background_android_service.R
import fi.iki.elonen.NanoHTTPD
import java.nio.charset.StandardCharsets


class NanoHTTPD(private val context: Context, port: Int) : NanoHTTPD("0.0.0.0", port) {
    private var serverPort = port

    fun serveStatusPage(): Response {
        val rawStream = context.resources.openRawResource(R.raw.server_status)
        val htmlTemplate = rawStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }

        val appName = context.getString(R.string.app_name)
        val html = htmlTemplate.replace("\$appName", appName)

        return newFixedLengthResponse(Response.Status.OK, "text/html", html)
    }

    fun serveRoutesPage(): Response {
        val rawStream = context.resources.openRawResource(R.raw.server_routes)
        val htmlTemplate = rawStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        val appName = context.getString(R.string.app_name)
        val html = htmlTemplate.replace("\$appName", appName)
        return newFixedLengthResponse(Response.Status.OK, "text/html", html)
    }

    override fun serve(session: IHTTPSession?): Response {
        val uri = session?.uri ?: "/"
        return when (uri) {
            "/" -> serveRoutesPage()
            "/status" -> serveStatusPage()
            else -> newFixedLengthResponse("Rota não encontrada")
        }
    }
}
