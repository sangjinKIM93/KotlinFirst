package com.example.kotlinfirst.BuyDeal.data

import io.realm.Realm
import io.realm.RealmResults

class BuyModelDao(private val realm: Realm) {

    fun getAllBuy(): RealmResults<BuyModelRealm>{
        return realm.where(BuyModelRealm::class.java)
            .findAll()
    }

    fun addOrUpdateBuy(buyModelRealm: BuyModelRealm){
        realm.executeTransaction{
            //
        }
    }
}