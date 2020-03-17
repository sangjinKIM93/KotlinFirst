package com.example.kotlinfirst.recordSell

import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinfirst.R
import kotlinx.android.synthetic.main.item_targetbuy.view.*

class TargetDataAdapter(val list:MutableList<TargetModel>): RecyclerView.Adapter<TargetDataViewHolder>(), View.OnCreateContextMenuListener{
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        var itemId : Long = v?.tag as Long
        var itemIdInt: Int = itemId.toInt()
        menu?.add(100, itemIdInt, 0, "삭제")

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TargetDataViewHolder {
        //필요할때마다 뷰홀더를 생성하는 역할
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_targetbuy, parent, false)

        view.setOnCreateContextMenuListener(this)   //기본적으로 longClick에 반응한다.

        view.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                var id : Long = v!!.tag as Long

                onItemClickListener?.invoke(id.toInt())

            }
        })
        return TargetDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: TargetDataViewHolder, position: Int) {
        //뷰홀더가 생성될때 실제 데이터를 넣어주는 역할
        holder.containerView.tvTargetDate.text = list[position].sellDate
        holder.containerView.tvTargetNum.text = list[position].buyNum!!.minus(list[position].buyNumSold!!).toString()
        holder.containerView.tvTargetPrice.text = list[position].buyPrice.toString()

        //View에 연동되는 객체를 개발자가 할당해 줄 수 있는 속성
        //여기서 포지션은 그냥 현재 화면에서의 포지션이 아니라 전체에서 가지는 포지션이구나
        holder.containerView.tag = getItemId(position)

        holder.containerView.etNumSell.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                //여기에 listener를 숨겨서 데이터를 넘긴다.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                list[position].sellNum = s.toString().toInt()

            }

        })
    }

    //list 각 항목에 대한 id를 부여하는 함수
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    var onItemClickListener: ((Int)->Unit)?=null


}