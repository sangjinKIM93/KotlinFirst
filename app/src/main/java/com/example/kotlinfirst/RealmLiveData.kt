package com.example.kotlinfirst

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults

class RealmLiveData<T: RealmObject> (private val realmResults: RealmResults<T>)
    : LiveData<RealmResults<T>>(){

    init{
        value = realmResults
    }

    //realmResult객체가 갱신될대마다 LiveData를 갱신해줄 수 있도록
    private val listener = RealmChangeListener<RealmResults<T>>{value = it}

    //LiveData가 활성화 될때(관찰자가 onStart or onResume에 있을때) realmResult에 리스너를 붙여줌
    override fun onActive() {
        super.onActive()
        realmResults.addChangeListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        realmResults.removeChangeListener(listener)
    }
}