package com.vicidroid.amalia.sample.api.themoviedb.discover

import retrofit2.Call
import retrofit2.http.GET


interface DiscoverTvApi {
    @GET("discover/tv?sort_by=popularity.desc&timezone=America%2FNew_York&include_null_first_air_dates=false")
    fun discover(): Call<DiscoverResponse>
}