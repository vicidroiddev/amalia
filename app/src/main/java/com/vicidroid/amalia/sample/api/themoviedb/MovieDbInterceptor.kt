package com.vicidroid.amalia.sample.api.themoviedb

import com.vicidroid.amalia.sample.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class MovieDbInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val url = request.url().newBuilder()
            .addQueryParameter("api_key", BuildConfig.themoviedbToken)
            .build()

        request = request.newBuilder().url(url).build()

        return chain.proceed(request)
    }
}
