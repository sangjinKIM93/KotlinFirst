package com.example.kotlinfirst.CurrentState

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinfirst.R
import com.example.kotlinfirst.RetrofitKotlin
import com.example.kotlinfirst.BuyDeal.data.BuyModelRealm
import com.example.kotlinfirst.BuyDeal.viewmodel.BuyViewModel
import com.example.kotlinfirst.SellDeal.data.SellModelRealm
import com.example.kotlinfirst.SellDeal.viewmodel.SellViewModel
import com.google.gson.GsonBuilder
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_total.*
import kotlinx.android.synthetic.main.fragment_total.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


open class TotalFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var v: View
    lateinit var realm: Realm

    lateinit var totalList: MutableList<TotalModel>
    lateinit var adapter: TotalDataAdapter

    lateinit var totalNameList: ArrayList<String>
    lateinit var totalCodeList: ArrayList<String>

    private val serverURL = "http://34.64.70.43/"


    fun newInstance(): TotalFragment {
        val args = Bundle()

        val frag = TotalFragment()
        frag.arguments = args

        return frag
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_total, container, false)
        mContext = requireContext()
        realm = Realm.getDefaultInstance()

        //Retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(serverURL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
        val retrofitService = retrofit.create(RetrofitKotlin::class.java)

        //리사이클러뷰 셋팅
        totalList = mutableListOf()
        adapter = TotalDataAdapter(totalList)
        v.totalRecycler.adapter = adapter
        v.totalRecycler.layoutManager = LinearLayoutManager(mContext)

        //데이터셋팅
        totalNameList = arrayListOf()
        totalCodeList = arrayListOf()
        //getTotalData()


        //크롤링 버튼
        v.btn_crawl.setOnClickListener {

            it.btn_crawl.shrink()   //버튼 줄어들기

            //버튼 이미지 변경 후 회전 에니메이션
            it.btn_crawl.setIconResource(R.drawable.ic_refresh)
            val rotate = ObjectAnimator.ofFloat(it.btn_crawl, View.ROTATION, -360f, 0f)
            rotate.setDuration(1000)
            rotate.repeatCount = ObjectAnimator.INFINITE
            rotate.interpolator = LinearInterpolator()
            rotate.start()


            var params: HashMap<String, String> = HashMap()

            params.put("size", totalList.size.toString())

            for (i in 0..totalList.size - 1) {
                params.put(i.toString(), totalList.get(i).totalCode.toString())
            }

            //1. retrofit과 통신하여 crawling한 데이터 가져오기
            retrofitService.getCrawl(params).enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("on Faliure : ", t.toString());
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    var result = response.body()?.string()

                    //받아온 데이터를 넣어서 출력
                    var jsonArray = JSONArray(result)
                    for (i in 0..jsonArray.length() - 1) {
                        var jsonObject = jsonArray.getJSONObject(i)
                        var item = jsonObject.getString("currentPrice")
                        var itemCleaned =
                            item.replace(",", "");      //숫자 사이에 , 있어서 제거 후에 int로 바꿔줘야함.
                        Log.d("item : ", itemCleaned)
                        totalList.get(i).totalCurrentPrice = itemCleaned.toInt()
                    }

                    adapter = TotalDataAdapter(totalList)
                    v.totalRecycler.adapter = adapter
                    v.totalRecycler.layoutManager = LinearLayoutManager(mContext)

                    //현재가 항목 추가하고 총액을 손익으로 이름 바꾸기
                    tv_currentPrice.visibility = View.VISIBLE
                    tv_total.text = "손익"

                    rotate.end()
                    it.btn_crawl.extend()   //버튼 다시 늘리기

                }
            })
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        getTotalData()
    }


    //data넣어주기
    private fun getTotalData() {

        totalList.clear()

        //리스트 만들기. 이거는 그냥 spinner 만들때 같이 하고 shared로 저장하자...
        var query = realm.where<BuyModelRealm>()
        var allDealData = query.findAll()

        var totalProfitData = 0

        var jsonArray = JSONArray(allDealData.asJSON())
        for (i in 0..jsonArray.length() - 1) {
            var item = jsonArray.getJSONObject(i)
            var buyName = item.getString("buyName")
            var buyCode = item.getString("buyCode")

            if (!totalNameList.contains(buyName)) totalNameList.add(buyName)   //buyName을 가지고 있지 않으면 list에 추가
            if (!totalCodeList.contains(buyCode)) totalCodeList.add(buyCode)   //종목코드도 마찬가지

        }


        //리스트 기반으로 종목별로 계산 해주기
        for (i in 0..totalNameList.size - 1) {

            var totalNum = 0
            var totalPrice = 0

            //buyData에서 계산
            var queryBuy = realm.where<BuyModelRealm>().equalTo("buyName", totalNameList[i])
            var targetDataBuy = queryBuy.findAll()
            var jsonArrayBuy = JSONArray(targetDataBuy.asJSON())

            for (i in 0..jsonArrayBuy.length() - 1) {
                var item = jsonArrayBuy.getJSONObject(i)

                totalPrice += item.getInt("buyPrice") * item.getInt("buyNum")
                totalNum += item.getInt("buyNum")
            }

            var totalAvgPrice = totalPrice/totalNum     //평균매입가격

            //sellData에서 num 빼주기
            var querySell = realm.where<SellModelRealm>().equalTo("sellName", totalNameList[i])
            var targetDataSell = querySell.findAll()
            var jsonArraySell = JSONArray(targetDataSell.asJSON())

            if (targetDataSell != null && targetDataSell.size > 0) {
                for (i in 0..jsonArraySell.length() - 1) {
                    var item = jsonArraySell.getJSONObject(i)

                    totalNum -= item.getInt("sellNum")

                }
            }

            if (totalNum > 0) {   //0보다 작으면 의미가 없지.

                //최종 데이터 추가
                totalList.add(
                    TotalModel(
                        totalNameList[i],
                        totalCodeList[i],
                        totalAvgPrice,
                        0,
                        totalNum,
                        totalAvgPrice * totalNum
                    )
                )

            }
        }

        adapter.notifyDataSetChanged()
    }


}