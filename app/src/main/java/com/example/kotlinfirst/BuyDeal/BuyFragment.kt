@file:Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER")

package com.example.kotlinfirst.BuyDeal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinfirst.R
import com.example.kotlinfirst.ETC.StockDataAdapter
import com.example.kotlinfirst.ETC.ViewDetailActivity
import com.example.kotlinfirst.model.BuyModelRealm
import com.example.kotlinfirst.model.NumberLimitModel
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONArray
import java.util.*

@Suppress("UNUSED_ANONYMOUS_PARAMETER", "NAME_SHADOWING")
open class BuyFragment: Fragment(){

    val context = this
    var date = ""
    lateinit var stockList: MutableList<BuyModelRealm>
    lateinit var adapter: StockDataAdapter
    lateinit var realm: Realm

    lateinit var dialog: AlertDialog.Builder

    var spinnerAdaper: ArrayAdapter<NumberLimitModel>? = null

    var isOpen = false // fab버튼 클릭되었는지 안 되었는지
    lateinit var mContext:Context
    lateinit var v: View

    fun newInstance(): BuyFragment
    {
        val args = Bundle()

        val frag = BuyFragment()
        frag.arguments = args

        return frag
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.activity_main, container, false)
        mContext = requireContext()

        //realm객체 선언
        realm = Realm.getDefaultInstance()

//        realm.executeTransaction{
//            realm.deleteAll()
//        }

        //리사이클러뷰 셋팅
        stockList = mutableListOf()
        adapter = StockDataAdapter(stockList)
        v.stockListView.adapter = adapter
        v.stockListView.layoutManager = LinearLayoutManager(mContext)

        //realmDB에서 자료가져오기
        getStockData()

        //리사이클러뷰 단일 항목 클릭에 대한 인터페이스 정의
        adapter.onItemClickListener = {
            val intent = Intent(mContext, ViewDetailActivity::class.java)
            intent.putExtra("stockName", stockList[it].buyName)
            startActivity(intent)
        }

        //fabButon 설정 및 리스너
        fabBtnSet()

        return v
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    var stockNameSelected = ""

    private fun fabBtnSet() {

        v.addBtn.setOnClickListener {

            //다이얼로그 설정
            dialog = AlertDialog.Builder(mContext)
            val edialog: LayoutInflater = LayoutInflater.from(mContext)
            val mView: View = edialog.inflate(R.layout.dialog_buy, null)

            dialog.setView(mView)
            val dialogReal = dialog.create()
            dialogReal.show()

            //datePicker 설치
            val datePicker = mView.findViewById<DatePicker>(R.id.date_picker)
            datePickerSet(mView, datePicker)

            //완료 버튼 클릭시 데이터 넣어주기
            var dialogBuyFinishBtn = mView.findViewById<Button>(R.id.dialogBuyFinishBtn)
            dialogBuyFinishBtn.setOnClickListener {
                val name = mView.findViewById<EditText>(R.id.stockName).text.toString()
                val price = mView.findViewById<EditText>(R.id.stockPrice).text.toString()
                val number =
                    mView.findViewById<EditText>(R.id.numberOfStock).text.toString()
                addList(name, price, number, date)

                dialogReal.dismiss()
            }

        }
    }


    private fun datePickerSet(view: View, datePicker: DatePicker) {
        //현재 날짜로 입력할시 날짜 설정
        //date 초기값
        val yearInit = datePicker.year.toString()
        val monthInit = datePicker.month.toString()
        val dayInit = datePicker.dayOfMonth.toString()
        date = "$yearInit-$monthInit-$dayInit"

        //datePicker 초기화 및 변경값 저장
        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            var month = month + 1
            val msg = "You Selected: $day/$month/$year"
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()

            //이거는 month 앞에 "0"이 안 붙는 형식이라 따로 처리해줌.
            if (month < 10) date = "$year-0$month-$day"
            else date = "$year-$month-$day"
        }
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
            stockList.add(
                BuyModelRealm(
                    item.getLong("_id"),
                    item.getString("buyName"),
                    item.getInt("buyPrice"),
                    item.getInt("buyNum"),
                    item.getInt("buyNumSold"),
                    item.getString("date")
                )
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
        val alertDialog = AlertDialog.Builder(mContext)
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
//
//        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.alert_dialog, null)
//        alertDialog.setView(view)
//        alertDialog.show()
        return true
    }


}