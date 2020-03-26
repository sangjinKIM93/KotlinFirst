package com.example.kotlinfirst

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

public interface RetrofitKotlin {

    @GET("/{user}")
    fun getCrawl(@Path("user") user: String) : Call<ResponseBody>

    @GET("/select/{company_name}")
    fun getStockCode(@Path("company_name") company_name: String) : Call<ResponseBody>
}