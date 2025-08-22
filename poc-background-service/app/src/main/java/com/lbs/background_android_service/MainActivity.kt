package com.lbs.background_android_service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lbs.background_android_service.service.BackgroundApi
import com.lbs.background_android_service.ui.theme.BackgroundandroidserviceTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.*
import com.lbs.background_android_service.service.ServiceStatusHolder


class MainActivity : ComponentActivity() {
    val logTag = "MainActivity"

    fun log (message:String) {
        Log.d(logTag, "$logTag : $message");
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(closeReceiver)
    }

    private val closeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish() // fecha activity
        }
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun createReceiver () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                closeReceiver,
                IntentFilter("com.lbs.background_android_service.CLOSE_ACTIVITY"),
                RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(
                closeReceiver,
                IntentFilter("com.lbs.background_android_service.CLOSE_ACTIVITY")
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        createReceiver()


        setContent {
            BackgroundandroidserviceTheme {
                MainScreen(
                    onStart = { port ->
                        log("Iniciando Serviço na porta: $port")
                        val intent = Intent(this, BackgroundApi::class.java)
                        intent.putExtra("httpServerPort",port)
                        startForegroundService(intent)
                    },
                    onStop = {
                        log("Parando Serviço")
                        val intent = Intent(this, BackgroundApi::class.java)
                        stopService(intent)
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onStart: (Int) -> Unit, onStop: () -> Unit) {
    var portText by remember { mutableStateOf("8080") }
    val serviceState by ServiceStatusHolder.status.collectAsState()

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF166534)
                ),
            )
        }
    ){ innerPadding ->
        Box( modifier = Modifier
            .background(Color(0xFFF2F0EF))
            .fillMaxSize()
            .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.width(240.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                StatusIndicator(
                    isRunning = serviceState.isRunning,
                    url = serviceState.url
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = portText,
                    onValueChange = { portText = it },
                    label = { Text("Porta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val port = portText.toIntOrNull()
                        if (port != null) {
                            onStart(port)
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Text(
                        "Iniciar Serviço",
                        fontSize = 20.sp
                    )
                }
                Button(
                    onClick = onStop,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))
                ) {
                    Text(
                        "Parar Serviço",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(isRunning: Boolean, url: String?) {
    val indicatorColor = if (isRunning) Color(0xFF16A34A) else Color(0xFFDC2626)
    val statusText = if (isRunning) url ?: "Serviço Online" else "Serviço Offline"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color = indicatorColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = statusText, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val logTag = "MainActivity"

    fun log (message:String) {
        Log.d(logTag, "$logTag : $message");
    }
    BackgroundandroidserviceTheme {
        MainScreen(
            onStart = { log("Serviço iniciado") },
            onStop = { log("Serviço parado") }
        )
    }
}