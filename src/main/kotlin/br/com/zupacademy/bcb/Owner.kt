package br.com.zupacademy.bcb

data class Owner (
    val name: String,
    val taxIdNumber: String,
){
    val type: String = "NATURAL_PERSON"
}