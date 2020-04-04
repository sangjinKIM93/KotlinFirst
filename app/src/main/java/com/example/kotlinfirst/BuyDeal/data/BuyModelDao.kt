package com.example.kotlinfirst.BuyDeal.data

import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where

class BuyModelDao(private val realm: Realm) {

    fun getAllBuy(): RealmResults<BuyModelRealm>{
        return realm.where(BuyModelRealm::class.java)
            .findAll()
    }

    fun selectTarget(name: String): RealmResults<BuyModelRealm>{
        return realm.where(BuyModelRealm::class.java).equalTo("buyName", name)
            .findAll()
    }

    fun addOrUpdateBuy(buyModelRealm: BuyModelRealm, name: String, code:String, price: String, number: String, date: String){
        realm.executeTransaction{
            //id autoIncrement
            var currentId = realm.where<BuyModelRealm>().max("_id")
            var nextId: Long
            if (currentId == null) {
                nextId = 1;
            } else {
                nextId = currentId.toLong() + 1
            }

            //StockModel에 추가
            buyModelRealm._id = nextId
            buyModelRealm.buyName = name
            buyModelRealm.buyCode = code
            buyModelRealm.buyPrice = price.toInt()
            buyModelRealm.buyNum = number.toInt()
            buyModelRealm.buyNumSold = 0
            buyModelRealm.date = date

            if(!buyModelRealm.isManaged){
                it.copyToRealm(buyModelRealm)
            }
        }
    }

    fun updateNumSold(buyModelRealm: BuyModelRealm, numSold: Int){
        realm.executeTransaction {
            buyModelRealm!!.buyNumSold = buyModelRealm.buyNumSold?.plus(numSold)
        }
    }


}