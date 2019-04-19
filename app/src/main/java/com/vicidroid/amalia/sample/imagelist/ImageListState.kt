package com.vicidroid.amalia.sample.imagelist

import com.vicidroid.amalia.core.ViewState

sealed class ImageListState : ViewState {
    class UrlsReady(val imageUrls: List<String>) : ImageListState()
}
