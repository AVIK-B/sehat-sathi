package com.ashaworker.triage.data.repository

import com.ashaworker.triage.data.model.LoginRequest
import com.ashaworker.triage.data.model.LoginResponse
import com.ashaworker.triage.data.model.ProtocolVersionResponse
import com.ashaworker.triage.data.model.VisitApiModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/v1/visits/batch")
    suspend fun uploadVisits(@Body visits: List<VisitApiModel>): Response<Unit>

    @GET("api/v1/protocols/version")
    suspend fun getProtocolVersion(): Response<ProtocolVersionResponse>
}
