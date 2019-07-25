package com.vicidroid.amalia.sample.api.themoviedb.discover

import com.vicidroid.amalia.sample.api.Retrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiscoverRepository(val api: DiscoverTvApi = Retrofit.instance.create(DiscoverTvApi::class.java)) {

    //TODO caching, db support, error handling
    suspend fun discoverFromApi(): List<DiscoverResult> {
        return withContext(Dispatchers.Default) {
            val response = api.discover().execute()

            when {
                response.isSuccessful -> response.body()!!.results
                else -> {
                    emptyList()
                }
            }
        }
    }
}