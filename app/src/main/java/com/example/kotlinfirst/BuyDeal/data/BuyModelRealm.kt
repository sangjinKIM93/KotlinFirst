package com.example.kotlinfirst.BuyDeal.data

import io.realm.RealmObject

open class BuyModelRealm(
    var _id: Long? = 0,
    var buyName:String? = null,
    var buyCode:String? = null,
    var buyPrice:Int? = 0,
    var buyNum:Int? = 0,
    var buyNumSold:Int? = 0,
    var date:String? = null
) : RealmObject()