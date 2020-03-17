package com.example.kotlinfirst.recordSell

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinfirst.R
import com.example.kotlinfirst.model.BuyModelRealm
import com.example.kotlinfirst.model.SellModelRealm
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_record_sell.*
import org.json.JSONArray
import java.util.*


@Suppress("CAST_NEVER_SUCCEEDS", "UNUSED_ANONYMOUS_PARAMETER", "NAME_SHADOWING")
class RecordSellActivity : AppCompatActivity() {

    var date = ""
    lateinit var realm: Realm
    var dealList = mutableListOf<String>()

    var spinnerAdaper: ArrayAdapter<String>? = null

    lateinit var targetList: MutableList<TargetModel>
    lateinit var adapter: TargetDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_sell)

        //realm객체 선언
        realm = Realm.getDefaultInstance()

        //spinnerDataSet
        getSpinnerData()

        //리사이클러뷰 셋팅
        targetList = mutableListOf()
        adapter = TargetDataAdapter(targetList)
        targetBuyRecycler.adapter = adapter
        targetBuyRecycler.layoutManager = LinearLayoutManager(this)

        //스피너와 데이트피커 설정
        spinnerSet()
        datePickerSet()

        fabButtonSet()



    }


    //FabButton 설정
    private fun fabButtonSet() {
        sellFinishBtn.setOnClickListener{

            var targetSize = targetList.size    //돌릴 리스트 갯수

            var sellPrice: String = stockPriceSell.text.toString()  //매도단가 가져오기
            var sellPriceInt: Int = sellPrice.toInt()

            for(i in 0..targetSize-1){
                var sellNum = adapter.list[i].sellNum as Int        //숫자가 0보다 크면 변경된 것으로 간주하고 DB에 추가한다.
                if(sellNum != null && sellNum > 0){
                    realm.executeTransaction {

                        //id autoIncrement
                        var currentId = realm.where<SellModelRealm>().max("_id")
                        var nextId: Long
                        if (currentId == null) {
                            nextId = 1;
                        } else {
                            nextId = currentId.toLong() + 1
                        }

                        //SellModel에 추가
                        val sellData = realm.createObject<SellModelRealm>() // Create a new object
                        sellData._id = nextId
                        sellData.sellName = stockNameSell.selectedItem.toString()
                        sellData.buyPrice = targetList[i].buyPrice
                        sellData.buyNum = targetList[i].buyNum
                        sellData.sellPrice = sellPriceInt
                        sellData.sellNum = sellNum
                        sellData.sellDate = date
                        sellData.buyIdx = targetList[i]._id!!.toInt()

                        //팔린 항목은 numsold 추가해주기
                        var query = realm.where<BuyModelRealm>().equalTo("_id", targetList[i]._id!!)
                        var targetBuyData = query.findFirst()
                        targetBuyData!!.buyNumSold = targetBuyData.buyNumSold?.plus(sellNum)   //분할매도할 수 있으니 판매량은 더해줘야해.

                        println("매도 자료 저장 $sellData")
                    }
                }

            }

            finish()

        }
    }


    //스피너에 넣어줄 데이터 가져오기
    private fun getSpinnerData() {
        dealList.clear()

        var query = realm.where<BuyModelRealm>()
        var allDealData = query.findAll()

        println("디비에 있는 데이터 $allDealData")

        var jsonArray = JSONArray(allDealData.asJSON())
        for (i in 0..jsonArray.length() - 1) {
            var item = jsonArray.getJSONObject(i)
            var companyName = item.getString("buyName")
            if(!dealList.contains(companyName)){
                dealList.add(companyName)
            }

        }
    }


    //datePicker 셋팅 및 설정
    private fun datePickerSet() {
        //datePicker 설치
        val datePicker = findViewById<DatePicker>(R.id.date_pickerSell)

        //date 초기값
        val yearInit = datePicker.year.toString()
        val monthInit = datePicker.month.toString()
        val dayInit = datePicker.dayOfMonth.toString()
        date = "$yearInit-$monthInit-$dayInit"

        //클릭시 date값 변환
        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            var month = month + 1
            val msg = "You Selected: $day/$month/$year"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }


    //스피너 셋팅 및 설정
    private fun spinnerSet() {
        //스피너 구현
        spinnerAdaper =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                dealList
            )
        stockNameSell.adapter = spinnerAdaper
        stockNameSell.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var companyName = dealList[position]
                    println("회사의 이름은 $companyName")
                    getStockData(companyName)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }
    }


    //spinner 선택되면 데이터 넣어주는 리스너
    private fun getStockData(targetName: String) {
        targetList.clear()

        var query = realm.where<BuyModelRealm>().equalTo("buyName", targetName)
        var allDealData = query.findAll()

        println("디비에 있는 데이터 $allDealData")

        var jsonArray = JSONArray(allDealData.asJSON())
        for (i in 0..jsonArray.length() - 1) {

            var item = jsonArray.getJSONObject(i)
            if(item.getInt("buyNum") > item.getInt("buyNumSold")){      //다 팔린 경우는 출력하지 않음.

                targetList.add(
                    TargetModel(
                        item.getLong("_id"),
                        item.getString("buyName"),
                        item.getInt("buyPrice"),
                        item.getInt("buyNum"),
                        item.getInt("buyNumSold"),
                        0,
                        item.getString("date"),
                        0
                    )
                )
            }

        }
        adapter.notifyDataSetChanged()

    }


}
