package com.example.kotlinfirst.SellDeal.data

import io.realm.RealmObject

open class SellModelRealm(
    var _id: Long? = 0,
    var sellName:String? = null,
    var buyPrice:Int? = 0,
    var buyNum:Int? = 0,
    var sellPrice:Int? = 0,
    var sellNum:Int? = 0,
    var sellDate:String? = null,
    var buyIdx:Int? = 0

) : RealmObject()