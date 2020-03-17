package com.example.kotlinfirst.BuyDeal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kotlinfirst.R

open class TotalFragment: Fragment(){

    fun newInstance(): TotalFragment
    {
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
        val v = inflater.inflate(R.layout.fragment_total, container, false)
        return v
    }
}