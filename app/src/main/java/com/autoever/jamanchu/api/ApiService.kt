package com.autoever.jamanchu.api

import com.autoever.jamanchu.models.Line
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("lines") // API 엔드포인트를 여기에 입력합니다.
    suspend fun getLines(): Response<List<Line>>

    @POST("lines")
    suspend fun createLine(@Body line: Line): Response<Line>

    @PUT("lines/{id}")
    suspend fun updateLine(@Path("id") id: String, @Body line: Line): Response<Line>

    @DELETE("lines/{id}")
    suspend fun deleteLine(@Path("id") id: String): Response<Unit>
}