package com.example.kotlinfirst

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.guideline_test.*

class GuideLineTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.guideline_test)

        digda.setOnClickListener{
            place_holder.setContentId(R.id.digda)
        }
    }
}
