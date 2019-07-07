package com.vicidroid.amalia.sample.api.themoviedb.discover

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiscoverResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "total_results") val totalResults: Int,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "results") val results: List<DiscoverResult>)


@JsonClass(generateAdapter = true)
data class DiscoverResult(
    @Json(name = "original_name") val originalName : String,
    @Json(name = "genre_ids") val genreIds : List<Int>,
    @Json(name = "name") val name : String,
    @Json(name = "popularity") val popularity : Double,
    @Json(name = "origin_country") val originCountry : List<String>,
    @Json(name = "vote_count") val voteCount : Int,
    @Json(name = "first_air_date") val firstAirDate : String,
    @Json(name = "backdrop_path") val backdropPath : String,
    @Json(name = "original_language") val originalLanguage : String,
    @Json(name = "id") val id : Int,
    @Json(name = "vote_average") val voteAverage : Double,
    @Json(name = "overview") val overview : String,
    @Json(name = "poster_path") val posterPath : String) {

    val postPathUrl = "https://image.tmdb.org/t/p/w500$posterPath"
}