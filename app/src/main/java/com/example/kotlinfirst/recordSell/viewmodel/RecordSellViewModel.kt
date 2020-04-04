package com.example.kotlinfirst.recordSell.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kotlinfirst.BuyDeal.data.BuyModelDao
import com.example.kotlinfirst.BuyDeal.data.BuyModelRealm
import com.example.kotlinfirst.RealmLiveData
import com.example.kotlinfirst.SellDeal.data.SellModelDao
import com.example.kotlinfirst.SellDeal.data.SellModelRealm
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where

class RecordSellViewModel : ViewModel() {

    private var sellModelRealm = SellModelRealm()

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val buyModelDao: BuyModelDao by lazy {
        BuyModelDao(realm)
    }

    private val sellModelDao: SellModelDao by lazy {
        SellModelDao(realm)
    }

    val buyLiveData: RealmLiveData<BuyModelRealm> by lazy {
        RealmLiveData(buyModelDao.getAllBuy())
    }

    val sellLiveData: RealmLiveData<SellModelRealm> by lazy {
        RealmLiveData(sellModelDao.getAllSell())
    }

    fun getTargetData(name: String): RealmResults<BuyModelRealm> {
        return buyModelDao.selectTarget(name)
    }

    fun addSellAndBuyNumSold(i: Int, name: String, sellName: String, sellPrice: Int, sellNum: Int, sellDate: String) {
        //타켓 리스트
        var targetList = getTargetData(name);
        //id autoIncrement
        var currentId = realm.where<SellModelRealm>().max("_id")
        var nextId: Long
        if (currentId == null) nextId = 1
        else nextId = currentId.toLong() + 1

        //Dao에서 db에 추가할 수 있게 넘겨주기
        sellModelDao.addOrUpdateSell(sellModelRealm, nextId, sellName, targetList[i]!!.buyPrice!!,
            targetList[i]!!.buyNum!!, sellPrice, sellNum, sellDate, targetList[i]!!._id!!.toInt())

        //팔린 항목은 numsold 추가해주기
        var query = realm.where<BuyModelRealm>().equalTo("_id", targetList[i]!!._id!!)
        var targetBuyData = query.findFirst()
        buyModelDao.updateNumSold(targetBuyData!!, sellNum)
    }

    fun addBuyNumSold(){

    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}