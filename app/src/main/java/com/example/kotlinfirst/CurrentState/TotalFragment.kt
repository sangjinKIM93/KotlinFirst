package com.example.kotlinfirst.CurrentState

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinfirst.R
import com.example.kotlinfirst.model.BuyModelRealm
import com.example.kotlinfirst.model.SellModelRealm
import com.example.kotlinfirst.model.TotalModel
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_total.view.*
import org.json.JSONArray

open class TotalFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var v: View
    lateinit var realm: Realm

    lateinit var totalList: MutableList<TotalModel>
    lateinit var adapter: TotalDataAdapter

    lateinit var totalNameList: ArrayList<String>


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

        //리사이클러뷰 셋팅
        totalList = mutableListOf()
        adapter = TotalDataAdapter(totalList)
        v.totalRecycler.adapter = adapter
        v.totalRecycler.layoutManager = LinearLayoutManager(mContext)

        //데이터셋팅
        totalNameList = arrayListOf()
        getTotalData()

        return v
    }


    //data넣어주기
    private fun getTotalData() {


        //리스트 만들기. 이거는 그냥 spinner 만들때 같이 하고 shared로 저장하자...
        var query = realm.where<BuyModelRealm>()
        var allDealData = query.findAll()

        println("디비에 있는 데이터 $allDealData")
        var totalProfitData = 0

        var jsonArray = JSONArray(allDealData.asJSON())
        for (i in 0..jsonArray.length() - 1) {
            var item = jsonArray.getJSONObject(i)
            var buyName = item.getString("buyName")

            if (!totalNameList.contains(buyName)) totalNameList.add(buyName)   //buyName을 가지고 있지 않으면 list에 추가

        }


        //리스트 기반으로 종목별로 계산 해주기
        for (i in 0..totalNameList.size - 1) {

            var totalNum = 0
            var totalPrice = 0
            var totalAvgPrice = 0

            //buyData에서 계산
            var queryBuy = realm.where<BuyModelRealm>().equalTo("buyName", totalNameList[i])
            var targetDataBuy = queryBuy.findAll()
            var jsonArrayBuy = JSONArray(targetDataBuy.asJSON())

            for (i in 0..jsonArrayBuy.length() - 1) {
                var item = jsonArrayBuy.getJSONObject(i)

                totalPrice = item.getInt("buyPrice")*item.getInt("buyNum")
                totalNum += item.getInt("buyNum")
                totalAvgPrice = (totalAvgPrice+totalPrice)/totalNum

            }

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

            if(totalNum > 0){   //0보다 작으면 의미가 없지.
                realm.executeTransaction {
                    //최종 데이터 추가
                    totalList.add(
                        TotalModel(
                            totalNameList[i],
                            totalAvgPrice,
                            0,
                            totalNum,
                            totalAvgPrice*totalNum
                        )
                    )
                }
            }
        }


        adapter.notifyDataSetChanged()

    }


}