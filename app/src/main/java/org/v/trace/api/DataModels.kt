package org.v.trace.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val success: Boolean,
    val data: List<TraceData>? = null,
    val count: Int? = null
)

@Serializable
data class TraceData(
    val phone: String? = null,
    val name: String? = null,
    val address: String? = null,
    val passport: String? = null,
    val birth_info: String? = null,
    val sim_id: String? = null
)
