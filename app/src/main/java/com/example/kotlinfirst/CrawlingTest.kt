package com.example.kotlinfirst

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_crawling_test.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CrawlingTest : AppCompatActivity() {

    private val serverURL = "http://34.64.70.43:5000/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crawling_test)

        //레트로핏 객체 및 레트로핏 서비스 객체 선언
        //레트로핏 객체 및 레트로핏 서비스 객체 선언
        val retrofit = Retrofit.Builder()
            .baseUrl(serverURL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
        val retrofitService = retrofit.create(RetrofitKotlin::class.java)


        btn_crawl.setOnClickListener{

            retrofitService.getCrawl("sangjin").enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    tv_crawl.text = t.toString()
                    Log.d("on Faliure : ", t.toString());
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    var result = response.body()?.string()
                    tv_crawl.text = result
                    Log.d("on success : ", result);
                }

            })

        }
    }
}


