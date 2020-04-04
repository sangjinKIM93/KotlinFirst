package com.example.kotlinfirst.BuyDeal

import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinfirst.ETC.DatePickerSet
import com.example.kotlinfirst.R
import com.example.kotlinfirst.RetrofitKotlin
import com.example.kotlinfirst.BuyDeal.data.BuyModelRealm
import com.google.gson.GsonBuilder
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_buy_add.*
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class BuyAddActivity : AppCompatActivity() {

    lateinit var realm: Realm
    var date = ""

    private val serverURL = "http://34.64.70.43/"
    var retrofitService: RetrofitKotlin? = null
    lateinit var dialog: AlertDialog.Builder

    lateinit var items: Array<String>
    lateinit var searchList: ArrayList<String>
    var nameSelected:String = ""
    var codeSelected:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_add)

        //realm객체 선언
        realm = Realm.getDefaultInstance()

        //레트로핏 객체 및 레트로핏 서비스 객체 선언
        //레트로핏 객체 및 레트로핏 서비스 객체 선언
        val retrofit = Retrofit.Builder()
            .baseUrl(serverURL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
        retrofitService = retrofit.create(RetrofitKotlin::class.java)

        //datePicker 설치 및 date 받기
        val datePicker = findViewById<DatePicker>(R.id.date_picker)
        val datePickerSetAdd = DatePickerSet()     //내가 만든 클래스에서 가져온거
        datePickerSetAdd.datePickerSet(datePicker)

        BuyFinishBtn.setOnClickListener{
            addList(nameSelected, codeSelected, stockPrice.text.toString(), numberOfStock.text.toString(), datePickerSetAdd.date);
        }

        btn_searchStock.setOnClickListener{

            searchList = arrayListOf()  //arrayList 초기화. 이거 안 해주면 .add()를 할 수 없음

            //Retrofit으로 종목번호 JSON정보 가져오기
            var stockName : String = stockName.text.toString()
            searchStock(stockName);

        }

    }


    //서버 db에서 종목 번호 검색
    private fun searchStock(stockName : String) {
        retrofitService?.getStockCode(stockName)?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("on Faliure : ", t.toString());
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                var result = response.body()?.string()
                Log.d("on success : ", result);

                //여기서 JSONArray 풀어주고 array 만들기
                var jsonArray = JSONArray(result)
                for(i in 0..jsonArray.length()-1){
                    var jsonObject = jsonArray.getJSONObject(i)
                    var name = jsonObject.getString("stockName")
                    var code = jsonObject.getString("stockCode")
                    var nameANDcode = name+"/"+code

                    searchList.add(nameANDcode)
                }

                //결과List를 Alert창으로 보여주기
                showListAlert();

            }

        })
    }

    //검색 결과 리스트로 보여주기
    private fun showListAlert(){
        items = searchList.toArray(arrayOfNulls<String>(searchList.size))
        val builder = AlertDialog.Builder(this@BuyAddActivity)
        with(builder)
        {
            setTitle("검색 결과")
            setItems(items){dialog, which ->

                val itemStr: String = items[which].toString()
                val divideNameCode = itemStr.split("/")
                nameSelected = divideNameCode[0]
                codeSelected = divideNameCode[1]

                stockName.setText(nameSelected)
                stockCode.setText(codeSelected)

            }
            show()
        }
    }

    //매수 기록 완료 버튼 누를시 자료 추가
    private fun addList(name: String, code:String, price: String, number: String, date: String) {

        //여기서 데이터 추가해주기
        realm.executeTransaction {
            //id autoIncrement
            var currentId = realm.where<BuyModelRealm>().max("_id")
            var nextId: Long
            if (currentId == null) {
                nextId = 1;
            } else {
                nextId = currentId.toLong() + 1
            }

            //StockModel에 추가
            val sellData = realm.createObject<BuyModelRealm>() // Create a new object
            sellData._id = nextId
            sellData.buyName = name
            sellData.buyCode = code
            sellData.buyPrice = price.toInt()
            sellData.buyNum = number.toInt()
            sellData.buyNumSold = 0
            sellData.date = date

            println(sellData)
        }
        finish()
    }


//    private fun datePickerSet(datePicker: DatePicker) {
//        //현재 날짜로 입력할시 날짜 설정
//        //date 초기값
//        val yearInit = datePicker.year.toString()
//        val monthInit = datePicker.month.toString()
//        val dayInit = datePicker.dayOfMonth.toString()
//        date = "$yearInit-$monthInit-$dayInit"
//
//        //datePicker 초기화 및 변경값 저장
//        val today = Calendar.getInstance()
//        datePicker.init(
//            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
//            today.get(Calendar.DAY_OF_MONTH)
//        ) { view, year, month, day ->
//            var month = month + 1
//            val msg = "You Selected: $day/$month/$year"
//            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
//
//            //이거는 month 앞에 "0"이 안 붙는 형식이라 따로 처리해줌.
//            if (month < 10) date = "$year-0$month-$day"
//            else date = "$year-$month-$day"
//        }
//    }
}
