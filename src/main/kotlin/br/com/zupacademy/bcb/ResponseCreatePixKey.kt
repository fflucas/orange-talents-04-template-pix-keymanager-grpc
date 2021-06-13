package br.com.zupacademy.bcb

import br.com.zupacademy.key.KeyType
import java.time.LocalDateTime

data class ResponseCreatePixKey(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)
