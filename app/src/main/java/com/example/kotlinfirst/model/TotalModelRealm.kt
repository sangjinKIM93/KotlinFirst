package com.example.kotlinfirst.model

import io.realm.RealmObject

open class TotalModelRealm(
    var _id: Long? = 0,
    var stockName:String? = null,
    var stockAvgPrice:Int? = 0,
    var stockTotalPrice:Int? = 0,
    var stockNum:Int? = 0
) : RealmObject()