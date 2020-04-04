package com.example.kotlinfirst.SellDeal.data


import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where

class SellModelDao(private val realm: Realm) {

    fun getAllSell(): RealmResults<SellModelRealm>{
        return realm.where(SellModelRealm::class.java)
            .findAll()
    }

    fun addOrUpdateSell(sellModelRealm: SellModelRealm, id: Long, sellName: String, buyPrice: Int, buyNum:Int, sellPrice:Int, sellNum:Int, sellDate:String, buyIdx:Int){
        realm.executeTransaction{

            //넘겨줄 SellModel 데이터 만들기
            sellModelRealm._id = id
            sellModelRealm.sellName = sellName
            sellModelRealm.buyPrice = buyPrice
            sellModelRealm.buyNum = buyNum
            sellModelRealm.sellPrice = sellPrice
            sellModelRealm.sellNum = sellNum
            sellModelRealm.sellDate = sellDate
            sellModelRealm.buyIdx = buyIdx

            if(!sellModelRealm.isManaged){
                it.copyToRealm(sellModelRealm)
            }
        }
    }


}