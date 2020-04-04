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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinfirst.R
import com.example.kotlinfirst.BuyDeal.adapter.StockDataAdapter
import com.example.kotlinfirst.BuyDeal.data.BuyModelRealm
import com.example.kotlinfirst.BuyDeal.viewmodel.BuyViewModel
import com.example.kotlinfirst.model.NumberLimitModel
import kotlinx.android.synthetic.main.activity_main.view.*

@Suppress("UNUSED_ANONYMOUS_PARAMETER", "NAME_SHADOWING")
open class BuyFragment: Fragment(){

    lateinit var stockList: MutableList<BuyModelRealm>
    lateinit var adapter: StockDataAdapter

    var spinnerAdaper: ArrayAdapter<NumberLimitModel>? = null

    lateinit var mContext:Context
    lateinit var v: View

    private var viewModel: BuyViewModel? = null

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

//        realm.executeTransaction{
//            realm.deleteAll()
//        }

        viewModel = activity!!.application!!.let{
            ViewModelProvider(
                activity!!.viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(it)
            ).get(BuyViewModel::class.java)
        }

        viewModel!!.let{
            it.buyLiveData.value?.let{
                adapter =
                    StockDataAdapter(it)
                v.stockListView.adapter = adapter
                v.stockListView.layoutManager = LinearLayoutManager(mContext)
            }
            it.buyLiveData.observe(viewLifecycleOwner,
                Observer {
                    adapter.notifyDataSetChanged()
                })
        }


        //리사이클러뷰 단일 항목 클릭에 대한 인터페이스 정의
        adapter.onItemClickListener = {
            val intent = Intent(mContext, ViewDetailActivity::class.java)
            intent.putExtra("stockName", stockList[it].buyName)
            startActivity(intent)
        }

        //fabButon 클릭시 매수 기록 activity로 이동
        v.addBtn.setOnClickListener {
            var intent = Intent(mContext, BuyAddActivity::class.java)
            startActivity(intent)
        }

        return v
    }



    override fun onContextItemSelected(item: MenuItem): Boolean {

        //해당 자료의 _id 뽑아내기
        var position = item.itemId
        var id = stockList.get(position)._id

        return true
    }


}