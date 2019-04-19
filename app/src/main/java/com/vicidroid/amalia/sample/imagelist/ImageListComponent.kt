package com.vicidroid.amalia.sample.imagelist

import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.vicidroid.amalia.core.LifecycleComponent
import java.util.*

class ImageListComponent : LifecycleComponent<ImageListState>() {

    var masterList = listOf(
        "https://cdn.motor1.com/images/mgl/lPoV1/s3/renders-of-cars-with-tiny-wheels.jpg",
        "https://s1.cdn.autoevolution.com/images/news/ferrari-812-spider-rendered-probably-wont-happen-115479_1.jpg",
        "https://amp.businessinsider.com/images/5a57df4ef421493e028b4752-960-720.jpg",
        "https://cmsimages-alt.kbb.com/content/dam/kbb-editorial/make/mazda/mazda3/2019/first-review/01-2019-Mazda3-first-review.jpg",
        "https://cdn.bmwblog.com/wp-content/uploads/2018/08/BMW-M5-Competition-test-drive99-830x553.jpg"
    )

    var imageList = emptyList<String>()

    override fun onCreate(owner: LifecycleOwner) {
        if (imageList.isEmpty()) {
            Toast.makeText(applicationContext, "No previous image list, shuffling...", Toast.LENGTH_SHORT).show()
            imageList = masterList.shuffled(Random(System.currentTimeMillis()))
        } else {
            Toast.makeText(applicationContext, "Restored image list", Toast.LENGTH_SHORT).show()
        }

        pushState(ImageListState.UrlsReady(imageList))
    }
}