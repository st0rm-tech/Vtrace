package org.v.trace.ui.screens

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
import kotlinx.coroutines.launch
import org.v.trace.api.ApiService
import org.v.trace.api.TraceData
import org.v.trace.ui.components.*

@Composable
fun MainScreen(apiService: ApiService) {
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
                errorMessage = "Kayıt bulunamadı."
            }
        } catch (e: Exception) {
            errorMessage = "Bir hata oluştu: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IOSSearchField(
                value = searchNumber,
                onValueChange = { searchNumber = it },
                onSearch = { scope.launch { performSearch() } },
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = { scope.launch { performSearch() } },
                modifier = Modifier.size(52.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF007AFF))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Search", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        errorMessage?.let {
            Text(text = it, color = Color.Red, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(resultList) { item ->
                ProfileCard(name = item.name ?: "Bilinmiyor")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                GlassCard {
                    ResultItem("Adres", item.address ?: "N/A", Icons.Default.Home)
                    Divider(color = Color(0x1AFFFFFF))
                    ResultItem("Pasaport", item.passport ?: "N/A", Icons.Default.Badge)
                    Divider(color = Color(0x1AFFFFFF))
                    ResultItem("Sim ID", item.sim_id ?: "N/A", Icons.Default.SimCard)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = "Ayarlar",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        GlassCard {
            Text(
                text = "Geliştirici Bilgileri",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            ResultItem("Developer DM", "@storm_inc", Icons.Default.Send)
            Divider(color = Color(0x1AFFFFFF))
            ResultItem("Kanal", "@shift_inc", Icons.Default.Groups)
        }
    }
}
