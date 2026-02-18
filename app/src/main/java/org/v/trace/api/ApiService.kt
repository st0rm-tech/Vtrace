package org.v.trace.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("index.php")
    suspend fun getTraceData(@Query("number") number: String): ApiResponse
}
