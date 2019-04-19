package com.vicidroid.amalia.sample.imagelist

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vicidroid.amalia.ext.componentProvider
import com.vicidroid.amalia.sample.R

import kotlinx.android.synthetic.main.activity_image_list.*
import kotlinx.android.synthetic.main.content_image_list.image_list_image_1 as image1
import kotlinx.android.synthetic.main.content_image_list.image_list_image_2 as image2
import kotlinx.android.synthetic.main.content_image_list.image_list_image_3 as image3
import kotlinx.android.synthetic.main.content_image_list.image_list_image_4 as image4
import kotlinx.android.synthetic.main.content_image_list.image_list_image_5 as image5

class ImageListActivity : AppCompatActivity() {

  private val imageListComponent by componentProvider { ImageListComponent() }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_image_list)
    setSupportActionBar(toolbar)

    imageListComponent.propagateStatesTo(::onImageListStateChanged)
  }

  private fun onImageListStateChanged(state: ImageListState) {
    // PRETEND LEGACY CODE WHICH IS STUCK IN ACTIVITY

    when (state) {
      is ImageListState.UrlsReady -> {
        Glide.with(this).load(state.imageUrls[0]).centerInside().into(image1)
        Glide.with(this).load(state.imageUrls[1]).centerInside().into(image2)
        Glide.with(this).load(state.imageUrls[2]).centerInside().into(image3)
        Glide.with(this).load(state.imageUrls[3]).centerInside().into(image4)
        Glide.with(this).load(state.imageUrls[4]).centerInside().into(image5)
      }
    }
  }
}
