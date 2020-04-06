package com.example.kotlinfirst.SellDeal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinfirst.R
import com.example.kotlinfirst.SellDeal.data.SellModelRealm
import com.example.kotlinfirst.SellDeal.viewmodel.SellViewModel
import com.example.kotlinfirst.recordSell.RecordSellActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_sell.*
import kotlinx.android.synthetic.main.fragment_sell.view.*
import org.json.JSONArray
import java.text.NumberFormat

open class SellFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var v: View

    lateinit var adapter: SellDataAdapter
    private var viewModel: SellViewModel?= null


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

        //fabButon 설정
        initSet()

        //vieModel셋팅
        viewModel = activity!!.application!!.let{
            ViewModelProvider(
                activity!!.viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(it)
            ).get(SellViewModel::class.java)
        }

        //리사이클러뷰 & 옵저버 셋팅
        viewModel!!.let{
            it.sellLiveData.value!!.let{
                adapter = SellDataAdapter(it)
                v.sellRecycler.adapter = adapter
                v.sellRecycler.layoutManager = LinearLayoutManager(mContext)

                var totalprofit = 0
                for(i in 0..it.size-1){
                    var profit = (it.get(i)!!.sellPrice!!.minus(it.get(i)!!.buyPrice!!))*(it.get(i)!!.sellNum!!)
                    totalprofit += profit
                }

                v.totalProfit.setText("총 손익 : ${totalprofit}")

            }
            it.sellLiveData.observe(viewLifecycleOwner,
                Observer {
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "매매 옵저버", Toast.LENGTH_SHORT).show()
                })
        }


        return v
    }


    private fun initSet() {
        //fab버튼 클릭시
        v.addSellBtn.setOnClickListener {
            var intent = Intent(mContext, RecordSellActivity::class.java)
            startActivity(intent)
        }

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
    }


    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}