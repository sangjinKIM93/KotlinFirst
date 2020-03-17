package com.example.kotlinfirst.SellDeal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinfirst.R
import com.example.kotlinfirst.model.SellModelRealm
import com.example.kotlinfirst.recordSell.RecordSellActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_sell.view.*
import org.json.JSONArray
import java.text.NumberFormat

open class SellFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var v: View

    lateinit var sellList: MutableList<SellModelRealm>
    lateinit var adapter: SellDataAdapter

    lateinit var realm: Realm


    fun newInstance(): SellFragment {
        val args = Bundle()

        val frag = SellFragment()
        frag.arguments = args

        return frag
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_sell, container, false)
        mContext = requireContext()

        //realm객체 선언
        realm = Realm.getDefaultInstance()

        //fabButon 설정 및 리스너
        fabBtnSet()

        //리사이클러뷰 셋팅
        sellList = mutableListOf()
        adapter = SellDataAdapter(sellList)
        v.sellRecycler.adapter = adapter
        v.sellRecycler.layoutManager = LinearLayoutManager(mContext)

        //자료가져오기
        getStockData()

        //스크롤시 버튼 숨기기
        v.sellRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    // 아래로 스크롤
                    v.addSellBtn.hide()
                    v.totalProfit.visibility = View.GONE
                } else if (dy < 0) {
                    // 위로 스크롤
                    v.addSellBtn.show()
                    v.totalProfit.visibility = View.VISIBLE
                }
            }
        })


        return v
    }


    private fun fabBtnSet() {
        v.addSellBtn.setOnClickListener {
            var intent = Intent(mContext, RecordSellActivity::class.java)
            startActivity(intent)
        }
    }


    private fun getStockData() {
        sellList.clear()

        var query = realm.where<SellModelRealm>()
        var allDealData = query.findAll()

        println("디비에 있는 데이터 $allDealData")
        var totalProfitData = 0

        var jsonArray = JSONArray(allDealData.asJSON())
        for (i in 0..jsonArray.length() - 1) {
            var item = jsonArray.getJSONObject(i)
            sellList.add(
                SellModelRealm(
                    item.getLong("_id"),
                    item.getString("sellName"),
                    item.getInt("buyPrice"),
                    item.getInt("buyNum"),
                    item.getInt("sellPrice"),
                    item.getInt("sellNum"),
                    item.getString("sellDate"),
                    item.getInt("buyIdx")
                )
            )

            totalProfitData += (item.getInt("sellPrice").minus(item.getInt("buyPrice")))*item.getInt("sellNum")

        }
        adapter.notifyDataSetChanged()

        //totalProfit 계산
        var totalProfitComma =  NumberFormat.getInstance().format(totalProfitData)
        v.totalProfit.setText("총 수익 : $totalProfitComma")

    }

    override fun onStart() {
        super.onStart()
        getStockData()
    }
}