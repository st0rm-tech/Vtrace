package org.v.trace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.v.trace.api.ApiService
import org.v.trace.api.TraceData
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
            TraceApp(apiService)
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
            if (response.success) {
                resultList = response.data ?: emptyList()
            } else {
                errorMessage = "API Error: Success failed"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = searchNumber,
            onValueChange = { searchNumber = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Numara yazın...") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                scope.launch { performSearch() }
            }),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(resultList) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ad: ${item.name ?: "N/A"}")
                        Text("Telefon: ${item.phone ?: "N/A"}")
                        Text("Adres: ${item.address ?: "N/A"}")
                        Text("Pasaport: ${item.passport ?: "N/A"}")
                        Text("Doğum: ${item.birth_info ?: "N/A"}")
                        Text("SimId: ${item.sim_id ?: "N/A"}")
                    }
                }
            }
        }
    }
}
