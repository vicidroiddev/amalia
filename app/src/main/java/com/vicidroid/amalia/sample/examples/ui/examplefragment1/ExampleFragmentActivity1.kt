package com.vicidroid.amalia.sample.examples.ui.examplefragment1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vicidroid.amalia.sample.R

class ExampleFragmentActivity1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_fragment_activity1)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ExampleFragment1.newInstance())
                .commitNow()
        }
    }

}
