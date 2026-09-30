package org.rust.starter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rust.starter.ui.theme.AndroidRustStarterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidRustStarterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RustGreeting(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RustGreeting(modifier: Modifier = Modifier) {
    val sum by remember { mutableIntStateOf(NativeBridge.add(10, 32)) }
    val greeting by remember { mutableStateOf(NativeBridge.greet("Android")) }

    // State untuk Progress & Button
    var progress by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = greeting)
        Text(text = "10 + 32 = $sum")

        Spacer(modifier = Modifier.height(32.dp))

        // Indikator Progress
        Text(text = "Progress Rust: $progress%")
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol untuk memicu task
        Button(
            onClick = {
                if (!isRunning) {
                    isRunning = true
                    scope.launch(Dispatchers.IO) {
                        NativeBridge.doHeavyTaskWithProgress { currentProgress ->
                            // Update state Compose secara thread-safe
                            progress = currentProgress
                        }
                        isRunning = false
                    }
                }
            },
            enabled = !isRunning
        ) {
            Text(if (isRunning) "Proses Berjalan..." else "Jalankan Heavy Task Rust")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RustGreetingPreview() {
    AndroidRustStarterTheme {
        RustGreeting()
    }
}