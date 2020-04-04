package com.example.kotlinfirst.SellDeal

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinfirst.R
import com.example.kotlinfirst.SellDeal.data.SellModelRealm
import kotlinx.android.synthetic.main.item_selllist.view.*

class SellDataAdapter(val list:MutableList<SellModelRealm>): RecyclerView.Adapter<SellDataViewHolder>(), View.OnCreateContextMenuListener{
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        var itemId : Long = v?.tag as Long
        var itemIdInt: Int = itemId.toInt()
        menu?.add(100, itemIdInt, 0, "삭제")

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellDataViewHolder {
        //필요할때마다 뷰홀더를 생성하는 역할
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_selllist, parent, false)

        view.setOnCreateContextMenuListener(this)   //기본적으로 longClick에 반응한다.

        view.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                var id : Long = v!!.tag as Long

                onItemClickListener?.invoke(id.toInt())

            }
        })
        return SellDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: SellDataViewHolder, position: Int) {
        //뷰홀더가 생성될때 실제 데이터를 넣어주는 역할
        holder.containerView.itemSellName.text = list[position].sellName
        holder.containerView.itemSellPrice.text = list[position].sellPrice.toString()
        holder.containerView.itemBuyPrice.text = list[position].buyPrice.toString()
        holder.containerView.itemSellNum.text = list[position].sellNum.toString()

        //profit 계산
        var profit = (list[position].sellPrice!!.minus(list[position].buyPrice!!))*list[position].sellNum!!
        holder.containerView.itemProfit.text = profit.toString()

        //View에 연동되는 객체를 개발자가 할당해 줄 수 있는 속성
        //여기서 포지션은 그냥 현재 화면에서의 포지션이 아니라 전체에서 가지는 포지션이구나
        holder.containerView.tag = getItemId(position)

    }

    //list 각 항목에 대한 id를 부여하는 함수
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    var onItemClickListener: ((Int)->Unit)?=null


}