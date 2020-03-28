package com.example.kotlinfirst.ETC

import android.widget.DatePicker
import java.util.*

public class DatePickerSet(var date: String = ""){


    public fun datePickerSet(datePicker: DatePicker){
        //현재 날짜로 입력할시 날짜 설정
        //date 초기값
        val yearInit = datePicker.year.toString()
        val monthInit = datePicker.month.plus(1).toString()
        val dayInit = datePicker.dayOfMonth.toString()
        date = "$yearInit-$monthInit-$dayInit"

        //datePicker 초기화 및 변경값 저장
        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            var month = month + 1
            date = "$year-$month-$day"
        }
    }

}