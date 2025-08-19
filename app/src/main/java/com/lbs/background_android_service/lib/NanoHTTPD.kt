package com.lbs.background_android_service.lib

import fi.iki.elonen.NanoHTTPD

class NanoHTTPD(port: Int) : NanoHTTPD("0.0.0.0", port) {
    private var serverPort = port

    override fun serve(session: IHTTPSession?): Response {
        val uri = session?.uri ?: "/"
        return when (uri) {
            "/" -> newFixedLengthResponse("Servidor NanoHTTPD rodando na porta: $serverPort")
            "/status" -> newFixedLengthResponse("OK")
            "/hello" -> {
                val params = session?.parameters ?: emptyMap()
                val name = params["name"]?.firstOrNull() ?: "mundo"
                newFixedLengthResponse("Olá, $name!")
            }

            else -> newFixedLengthResponse("Rota não encontrada")
        }
    }
}
