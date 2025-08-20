package com.lbs.poc_bgs_front

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.lbs.poc_bgs_front.screens.MainScreenActivity
import com.lbs.poc_bgs_front.ui.theme.ScreenContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

enum class DeviceType(val url: String) {
    EMULATOR("http://10.0.2.16:8080"),
    PHYSICAL_DEVICE("http://10.0.2.16:8080") //Tem que colocar o ip manualmente aqui pq vai se comportar parecido com o pc que tem aquele ip normal de 192.168
}

class StartActivity : ComponentActivity() {
    private val deviceType = DeviceType.EMULATOR


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LoadingScreen()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            checkServer()
        }
    }

    private suspend fun checkServer() {
        while (true) {
            val ok = try {
                val url = URL("${deviceType.url}/status")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 1000
                conn.requestMethod = "GET"
                conn.responseCode == 200
            } catch (e: Exception) {
                Log.d("StartActivity", "checkServer: ${e.message}" )
                false
            }

            if (ok) {
                launchMain()
                break
            } else {
                openBackgroundApp()
                // espera um pouco antes de tentar de novo
                delay(2000)
            }
        }
    }

    private fun launchMain() {
        runOnUiThread {
            startActivity(Intent(this, MainScreenActivity::class.java))
            finish()
        }
    }

    private fun openBackgroundApp() {
        try {
            val intent = Intent()
            intent.setClassName(
                "com.lbs.background_android_service",
                "com.lbs.background_android_service.MainActivity"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun LoadingScreen() {
    ScreenContainer {
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(20.dp))
        Text("Inicializando serviços...")
        }
    }
}


@Preview
@Composable
fun PreviewLoadingScreen () {
    LoadingScreen()
}