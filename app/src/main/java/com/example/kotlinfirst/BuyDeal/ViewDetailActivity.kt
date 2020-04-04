package com.example.kotlinfirst.BuyDeal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinfirst.R
import kotlinx.android.synthetic.main.view_detail.*

class ViewDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_detail)

        val intent = getIntent()
        viewDetailName.text = intent.getStringExtra("stockName")
    }
}