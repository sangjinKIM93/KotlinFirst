package com.example.kotlinfirst

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

public interface RetrofitKotlin {

    //종목번호 가져오기
    @GET("/select/{company_name}")
    fun getStockCode(@Path("company_name") company_name: String) : Call<ResponseBody>

    //현재 가격 가져오기
    @FormUrlEncoded
    @POST("/crawl")
    fun getCrawl(@FieldMap parameters: HashMap<String, String>) : Call<ResponseBody>


}