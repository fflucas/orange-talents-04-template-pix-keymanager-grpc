package br.com.zupacademy.bcb

data class RequestCreatePixKey(
    val keyType: String,
    val key: String?,
    val bankAccount: BankAccount,
    val owner: Owner,
)