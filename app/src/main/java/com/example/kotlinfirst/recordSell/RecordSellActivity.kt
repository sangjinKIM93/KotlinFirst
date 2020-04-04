package com.example.kotlinfirst.recordSell

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinfirst.ETC.DatePickerSet
import com.example.kotlinfirst.R
import com.example.kotlinfirst.BuyDeal.data.BuyModelRealm
import com.example.kotlinfirst.SellDeal.data.SellModelRealm
import com.example.kotlinfirst.recordSell.viewmodel.RecordSellViewModel
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_record_sell.*
import org.json.JSONArray


@Suppress("CAST_NEVER_SUCCEEDS", "UNUSED_ANONYMOUS_PARAMETER", "NAME_SHADOWING")
class RecordSellActivity : AppCompatActivity() {

    lateinit var realm: Realm
    var dealList = mutableListOf<String>()

    var spinnerAdaper: ArrayAdapter<String>? = null

    lateinit var targetList: MutableList<TargetModel>
    lateinit var adapter: TargetDataAdapter
    lateinit var datePickerSetSell: DatePickerSet

    private var viewModel : RecordSellViewModel?= null

    lateinit var companyName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_sell)

        //realm객체 선언
        realm = Realm.getDefaultInstance()

        //datePicker 설치 및 date 받기
        val datePicker = findViewById<DatePicker>(R.id.date_pickerSell)
        datePickerSetSell = DatePickerSet()
        datePickerSetSell.datePickerSet(datePicker)

        //spinner에 들어갈 데이터 가져오기 & 스피너 선택시 리스트 변경
        getSpinnerData()
        spinnerSet()

        //viewModel셋팅
        viewModel = application!!.let{
            ViewModelProvider(
                viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(it)
            ).get(RecordSellViewModel::class.java)
        }

        //target 종목 매수 데이터 리사이클러뷰 셋팅
        targetList = mutableListOf()
        adapter = TargetDataAdapter(targetList)
        targetBuyRecycler.adapter = adapter
        targetBuyRecycler.layoutManager = LinearLayoutManager(this)


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

                    viewModel!!.let{
                        it.addSellAndBuyNumSold(i, companyName, stockNameSell.selectedItem.toString(),
                            sellPriceInt, sellNum, datePickerSetSell.date)
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
                    companyName = dealList[position]
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
