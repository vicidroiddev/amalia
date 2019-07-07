package com.vicidroid.amalia.sample.api

import com.vicidroid.amalia.sample.api.themoviedb.MovieDbInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Retrofit {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(MovieDbInterceptor())
        .build()

    val instance = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()
}