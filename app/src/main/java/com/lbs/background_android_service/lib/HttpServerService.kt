package com.lbs.background_android_service.lib

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

class HttpServerService(
    private val port: Int = 8080,
    private val log: (String) -> Unit
) {
    private var serverSocket: ServerSocket? = null
    var isRunning = false
    private val executor = Executors.newFixedThreadPool(4)
    private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun start() {
        if (isRunning) {
            log("Em execução na porta: $port")
            return
        }

        serverScope.launch {
            try {
                serverSocket = ServerSocket(port)
                isRunning = true
                log("Servidor HTTP iniciado na porta $port")

                while (isRunning) {
                    try {
                        val client = serverSocket?.accept()
                        client?.let { socket ->
                            executor.execute { handleClient(socket) }
                        }
                    } catch (e: IOException) {
                        if (isRunning) log("Erro ao aceitar conexão: ${e.message}")
                    }
                }
            } catch (e: IOException) {
                log("Erro ao iniciar servidor: ${e.message}")
            }
        }
    }

    fun stop() {
        if (!isRunning) return
        isRunning = false
        try {
            serverSocket?.close()
            log("Servidor HTTP parado")
        } catch (e: IOException) {
            log("Erro ao parar servidor: ${e.message}")
        } finally {
            executor.shutdown()
            serverScope.cancel()
        }
    }

    private fun handleClient(socket: Socket) {
        try {
            val input = socket.getInputStream().bufferedReader()
            val output = socket.getOutputStream()

            val request = input.readLine()
            log("Requisição recebida: $request")

            val response = when {
                request?.startsWith("GET /") == true -> handleGet(request)
                request?.startsWith("POST /") == true -> handlePost(request, input)
                else -> "HTTP/1.1 400 Bad Request\r\n\r\nMétodo não suportado"
            }

            output.write(response.toByteArray())
            output.flush()
        } catch (e: IOException) {
            log("Erro ao processar cliente: ${e.message}")
        } finally {
            try {
                socket.close()
            } catch (e: IOException) {
                log("Erro ao fechar socket: ${e.message}")
            }
        }
    }

    private fun handleGet(request: String): String {
        val path = request.split(" ")[1]
        return when (path) {
            "/" -> "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" +
                    "<html><body><h1>Servidor MARCOLA IA</h1><p>RODANDO SERVIDOR CUSTOM</p></body></html>"

            "/status" -> "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n" +
                    "{\"status\": \"online\", \"port\": $port, \"timestamp\": ${System.currentTimeMillis()}}"

            "/api/hello" -> "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n" +
                    "{\"message\": \"Olá do servidor Android!\", \"timestamp\": ${System.currentTimeMillis()}}"

            else -> "HTTP/1.1 404 Not Found\r\n\r\nEndpoint não encontrado"
        }
    }

    private fun handlePost(request: String, input: BufferedReader): String {
        val path = request.split(" ")[1]
        return when (path) {
            "/api/echo" -> {
                val body = input.readText()
                "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n" +
                        "{\"echo\": \"$body\", \"timestamp\": ${System.currentTimeMillis()}}"
            }
            else -> "HTTP/1.1 404 Not Found\r\n\r\nEndpoint não encontrado"
        }
    }
}
