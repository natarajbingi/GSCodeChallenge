package com.gms.nasapi.network

import com.gms.nasapi.utils.NasaApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIServices {
    // SEARCH from start date
    @GET("apod?api_key=DEMO_KEY")
    fun getNasaAPODdataList(
        @Query("start_date") start_date: String
    ): Call<List<NasaApiResponse>>

    // SEARCH from start date
    @GET("apod?api_key=DEMO_KEY")
    fun getNasaAPODdataDate(
        @Query("date") date: String
    ): Call<NasaApiResponse>

}