package org.v.trace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
                Surface(modifier = Modifier.fillMaxSize(), color = Charcoal) {
                    TraceApp(apiService)
                }
            }
        }
    }
}

@Composable
fun TraceApp(apiService: ApiService) {
    var searchNumber by remember { mutableStateOf("") }
    var resultList by remember { mutableStateOf<List<TraceData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    
    suspend fun performSearch() {
        if (searchNumber.isBlank()) return
        isLoading = true
        errorMessage = null
        try {
            val response = apiService.getTraceData(searchNumber)
            if (response.success && !response.data.isNullOrEmpty()) {
                resultList = response.data
            } else {
                resultList = emptyList()
                errorMessage = "Veri bulunamadı."
            }
        } catch (e: Exception) {
            errorMessage = "Hata oluştu: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "V-Trace",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top = 40.dp, bottom = 40.dp)
        )

        IOSSearchField(
            value = searchNumber,
            onValueChange = { searchNumber = it },
            onSearch = { scope.launch { performSearch() } }
        )

        Spacer(modifier = Modifier.height(16.dp))

        IOSButton(
            text = "Sorgula",
            onClick = { scope.launch { performSearch() } },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))

        errorMessage?.let {
            Text(text = it, color = Color.Red, fontSize = 14.sp)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(resultList) { item ->
                Text(
                    text = "Arama Sonucu",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                GlassCard {
                    ResultItem("Ad Soyad", item.name ?: "N/A")
                    Divider(color = Color(0x1AFFFFFF))
                    ResultItem("Telefon", item.phone ?: "N/A")
                    Divider(color = Color(0x1AFFFFFF))
                    ResultItem("Adres", item.address ?: "N/A")
                    Divider(color = Color(0x1AFFFFFF))
                    ResultItem("Pasaport", item.passport ?: "N/A")
                    Divider(color = Color(0x1AFFFFFF))
                    ResultItem("Doğum", item.birth_info ?: "N/A")
                    Divider(color = Color(0x1AFFFFFF))
                    ResultItem("Sim ID", item.sim_id ?: "N/A")
                }
            }
        }
    }
}
