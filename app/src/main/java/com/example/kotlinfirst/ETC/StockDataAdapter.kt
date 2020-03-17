package com.example.kotlinfirst.ETC

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinfirst.R
import com.example.kotlinfirst.model.BuyModelRealm
import kotlinx.android.synthetic.main.item_mystock.view.*

class StockDataAdapter(val list:MutableList<BuyModelRealm>): RecyclerView.Adapter<StockDataViewHolder>(), View.OnCreateContextMenuListener{
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        var itemId : Long = v?.tag as Long
        var itemIdInt: Int = itemId.toInt()
        menu?.add(100, itemIdInt, 0, "삭제")

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockDataViewHolder {
        //필요할때마다 뷰홀더를 생성하는 역할
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mystock, parent, false)

        view.setOnCreateContextMenuListener(this)   //기본적으로 longClick에 반응한다.

        view.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                var id : Long = v!!.tag as Long
//                if(selectionList.contains(id)) selectionList.remove(id)
//                else selectionList.add(id as Long)
//                notifyDataSetChanged()
//                onItemSelectionChangedListener?.let{it(selectionList)} //변경된 사항을 리스너에 넘겨준다.

                onItemClickListener?.invoke(id.toInt())

            }
        })
        return StockDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: StockDataViewHolder, position: Int) {
        //뷰홀더가 생성될때 실제 데이터를 넣어주는 역할
        holder.containerView.tvType.text = "매수"
        holder.containerView.tvStockName.text = list[position].buyName
        holder.containerView.tvStockPrice.text = list[position].buyPrice.toString()
        holder.containerView.tvDate.text = list[position].date

        //View에 연동되는 객체를 개발자가 할당해 줄 수 있는 속성
        //여기서 포지션은 그냥 현재 화면에서의 포지션이 아니라 전체에서 가지는 포지션이구나
        holder.containerView.tag = getItemId(position)

        //activated되면 selectionList에 넣어주기
        holder.containerView.isActivated = selectionList.contains(getItemId(position))
    }

    //list 각 항목에 대한 id를 부여하는 함수
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    val selectionList = mutableListOf<Long>()  //선택된 항목 담는 리스트

    //onItemSelectionChangedListener라는 인터페이스를 생성 / 람다식으로
    var onItemSelectionChangedListener : ((MutableList<Long>)->Unit)?=null

    var onItemClickListener: ((Int)->Unit)?=null


}