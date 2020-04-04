package com.example.kotlinfirst.BuyDeal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kotlinfirst.BuyDeal.data.BuyModelDao
import com.example.kotlinfirst.BuyDeal.data.BuyModelRealm
import com.example.kotlinfirst.RealmLiveData
import io.realm.Realm

class BuyViewModel : ViewModel() {

    private val realm: Realm by lazy{
        Realm.getDefaultInstance()
    }

    private val buyModelDao : BuyModelDao by lazy{
        BuyModelDao(realm)
    }

    val buyLiveData: RealmLiveData<BuyModelRealm> by lazy{
        RealmLiveData(buyModelDao.getAllBuy())
    }

    fun addOrUpdateBuy(buyModelRealm: BuyModelRealm){
        //
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}