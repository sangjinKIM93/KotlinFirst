package com.example.kotlinfirst.ETC

//이거는 왜 만드는거야? 데이터가 여러개 들어갈때?
//표시되는 문자열과 실제 사용하려는 데이터가 다르다면?
//예를 들어, 매수 버튼을 누르면 숫자 1이 반환되도록 하고 싶다면?
class ProductData(var name:String, var price:Int){
    override fun toString(): String {
        return name
    }
}