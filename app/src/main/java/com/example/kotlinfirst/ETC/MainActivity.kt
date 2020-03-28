package com.example.kotlinfirst.ETC

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinfirst.R
import com.example.kotlinfirst.model.BuyModelRealm
import com.example.kotlinfirst.model.NumberLimitModel
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.util.*

class MainActivity : AppCompatActivity() {

    val context = this
    var date = ""
    lateinit var stockList: MutableList<BuyModelRealm>
    lateinit var adapter: StockDataAdapter
    lateinit var realm: Realm

    lateinit var dialog: AlertDialog.Builder

    var dealList = mutableListOf<NumberLimitModel>()
    var spinnerAdaper: ArrayAdapter<NumberLimitModel>? = null

    var isOpen = false // fab버튼 클릭되었는지 안 되었는지

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //realm객체 선언
        realm = Realm.getDefaultInstance()

        //리사이클러뷰 셋팅
        stockList = mutableListOf()
        adapter = StockDataAdapter(stockList)
        stockListView.adapter = adapter
        stockListView.layoutManager = LinearLayoutManager(this)

        //realmDB에서 자료가져오기
        getStockData()

        //리사이클러뷰 단일 항목 클릭에 대한 인터페이스 정의
        adapter.onItemClickListener = {
            val intent = Intent(applicationContext, ViewDetailActivity::class.java)
            intent.putExtra("stockName", stockList[it].buyName)
            startActivity(intent)
        }

        //fabButon 설정 및 리스너 + 매수&매도 버튼
        fabBtnSet()

        realm.executeTransaction{
            realm.deleteAll()
        }

        //다중 클릭 동작구현 / 람다식으로 구현한거야
//        adapter.onItemSelectionChangedListener = {
//            println("선택 항목 id : $it")
//        }

    }


    var stockNameSelected = ""

    private fun fabBtnSet() {
//        val fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
//        val fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)
//        val fabRClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise)
//        val fabRAntiClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise)
//
//        addBtn.setOnClickListener {
//            if (isOpen) {
//                sellBtn.startAnimation(fabClose)
//                buyBtn.startAnimation(fabClose)
//                addBtn.startAnimation(fabRClockwise)
//
//                isOpen = false
//            } else {
//                sellBtn.startAnimation(fabOpen)
//                buyBtn.startAnimation(fabOpen)
//                addBtn.startAnimation(fabRAntiClockwise)
//
//                sellBtn.isClickable
//                buyBtn.isClickable
//
//                isOpen = true
//
//                //매수기록 버튼 클릭시
//                buyBtn.setOnClickListener {
//                    //다이얼로그 설정
//                    dialog = AlertDialog.Builder(context)
//                    val edialog: LayoutInflater = LayoutInflater.from(context)
//                    val mView: View = edialog.inflate(R.layout.activity_buy_add, null)
//
//                    dialog.setView(mView)
//                    val dialogReal = dialog.create()
//                    dialogReal.show()
//
//                    //datePicker 설치
//                    val datePicker = mView.findViewById<DatePicker>(R.id.date_picker)
//                    datePickerSet(mView, datePicker)
//
//                    //완료 버튼 클릭시 데이터 넣어주기
//                    var dialogBuyFinishBtn = mView.findViewById<Button>(R.id.dialogBuyFinishBtn)
//                    dialogBuyFinishBtn.setOnClickListener {
//                        val name = mView.findViewById<EditText>(R.id.stockName).text.toString()
//                        val price = mView.findViewById<EditText>(R.id.stockPrice).text.toString()
//                        val number =
//                            mView.findViewById<EditText>(R.id.numberOfStock).text.toString()
//                        addList(name, price, number, date)
//
//                        dialogReal.dismiss()
//                    }
//                }
//
//                //매도기록 버튼 클릭시
//                sellBtn.setOnClickListener {
//
//                    var intent = Intent(this, RecordSellActivity::class.java)
//                    startActivity(intent)
//
//                }
//            }
//        }
    }





    //매수 기록 완료 버튼 누를시 자료 추가
    private fun addList(name: String, price: String, number: String, date: String) {

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
            sellData.buyPrice = price.toInt()
            sellData.buyNum = number.toInt()
            sellData.buyNumSold = 0
            sellData.date = date

            println(sellData)
            getStockData()
        }

        adapter.notifyDataSetChanged()
    }


    //데이터 가져오는 함수 하나 만들기
    private fun getStockData() {
        stockList.clear()
        spinnerAdaper?.clear()

        var query = realm.where<BuyModelRealm>()
        var allDealData = query.findAll()

        println("디비에 있는 데이터 $allDealData")

        var jsonArray = JSONArray(allDealData.asJSON())
        for (i in 0..jsonArray.length() - 1) {
            var item = jsonArray.getJSONObject(i)
//            stockList.add(
//                BuyModelRealm(
//                    item.getLong("_id"),
//                    item.getString("buyName"),
//                    item.getInt("buyPrice"),
//                    item.getInt("buyAvgPrice"),
//                    item.getInt("buyNum"),
//                    item.getInt("buyNumSold"),
//                    item.getString("date")
//                )
//            )
            dealList.add(
                NumberLimitModel(item.getString("buyName"), item.getInt("buyPrice"))
            )
        }
        adapter.notifyDataSetChanged()
        spinnerAdaper?.notifyDataSetChanged()
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {

        //해당 자료의 _id 뽑아내기
        var position = item.itemId
        var id = stockList.get(position)._id

        //삭제 (다이얼로그로 묻기)
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("정말로 삭제하시겠습니까?")
            .setPositiveButton("예") { dialog, which ->
                val result = realm.where<BuyModelRealm>().equalTo("_id", id).findFirst()
                realm.executeTransaction {
                    //stockList 삭제
                    result?.deleteFromRealm()
                    println("삭제가 완료되었습니다.")
                    getStockData()
                }
            }
            .setNeutralButton("아니요", null)
            .create()

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_dialog, null)
        alertDialog.setView(view)
        alertDialog.show()
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}
