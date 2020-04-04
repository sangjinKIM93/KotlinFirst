package com.example.kotlinfirst.SellDeal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kotlinfirst.RealmLiveData
import com.example.kotlinfirst.SellDeal.data.SellModelDao
import com.example.kotlinfirst.SellDeal.data.SellModelRealm
import io.realm.Realm

class SellViewModel :ViewModel() {

    private val realm: Realm by lazy{
        Realm.getDefaultInstance()
    }

    private val sellModelDao : SellModelDao by lazy{
        SellModelDao(realm)
    }

    val sellLiveData: RealmLiveData<SellModelRealm> by lazy{
        RealmLiveData(sellModelDao.getAllSell())
    }

//    fun addSell(sellModelRealm: SellModelRealm){
//        sellModelDao.addOrUpdateSell(sellModelRealm)
//    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

}

