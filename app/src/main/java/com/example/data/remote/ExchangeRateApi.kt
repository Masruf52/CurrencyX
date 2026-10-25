package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

@JsonClass(generateAdapter = true)
data class ExchangeRateResponse(
    @Json(name = "result") val result: String? = null,
    @Json(name = "base_code") val baseCode: String? = null,
    @Json(name = "rates") val rates: Map<String, Double>? = null,
    @Json(name = "time_last_update_unix") val timeLastUpdateUnix: Long? = null
)

interface ExchangeRateApi {

    @GET("v6/latest/{base}")
    suspend fun getLatestRates(@Path("base") base: String = "USD"): Response<ExchangeRateResponse>
}
