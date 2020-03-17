package com.example.kotlinfirst.BuyDeal

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kotlinfirst.CurrentState.TotalFragment
import com.example.kotlinfirst.SellDeal.SellFragment

class PagerAdapter(fm: FragmentManager) :FragmentPagerAdapter(fm){
    val PAGE_MAX_CNT = 3

    override fun getCount(): Int {
        return PAGE_MAX_CNT
    }

    override fun getItem(position: Int): Fragment {
        val fragment = when(position)
        {
            1 -> SellFragment().newInstance()
            2 -> TotalFragment().newInstance()
            else -> BuyFragment().newInstance()
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val title = when(position)
        {
            1 -> "매도"
            2 -> "종합"
            else -> "매수"
        }
        return title
    }
}