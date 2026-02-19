package org.v.trace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.v.trace.api.ApiService
import org.v.trace.api.TraceData
import org.v.trace.ui.components.GlassCard
import org.v.trace.ui.components.IOSButton
import org.v.trace.ui.components.IOSSearchField
import org.v.trace.ui.components.ResultItem
import org.v.trace.ui.theme.Charcoal
import org.v.trace.ui.theme.DarkColorScheme
import retrofit2.Retrofit
import org.v.trace.ui.screens.MainScreen
import org.v.trace.ui.screens.SettingsScreen
import org.v.trace.ui.theme.IOSBlue

enum class Screen { Main, Settings }

class MainActivity : ComponentActivity() {
    companion object {
        init {
            System.loadLibrary("vtrace")
        }
    }

    private external fun getBaseUrl(): String

    private val apiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = DarkColorScheme) {
                var currentScreen by remember { mutableStateOf(Screen.Main) }
                
                Scaffold(
                    containerColor = Charcoal,
                    bottomBar = {
                        VTraceBottomBar(
                            currentScreen = currentScreen,
                            onScreenSelected = { currentScreen = it }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.Main -> MainScreen(apiService)
                            Screen.Settings -> SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VTraceBottomBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF1C1C1E).copy(alpha = 0.8f),
        tonalElevation = 0.dp,
        modifier = Modifier.height(84.dp)
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.Main,
            onClick = { onScreenSelected(Screen.Main) },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { 
                if (currentScreen == Screen.Main) {
                    Text("Ana ekran", fontWeight = FontWeight.Medium)
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IOSBlue,
                unselectedIconColor = Color.Gray,
                selectedTextColor = IOSBlue,
                indicatorColor = Color.Transparent
            )
        )
        
        NavigationBarItem(
            selected = currentScreen == Screen.Settings,
            onClick = { onScreenSelected(Screen.Settings) },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { 
                if (currentScreen == Screen.Settings) {
                    Text("Ayarlar", fontWeight = FontWeight.Medium)
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IOSBlue,
                unselectedIconColor = Color.Gray,
                selectedTextColor = IOSBlue,
                indicatorColor = Color.Transparent
            )
        )
    }
}
