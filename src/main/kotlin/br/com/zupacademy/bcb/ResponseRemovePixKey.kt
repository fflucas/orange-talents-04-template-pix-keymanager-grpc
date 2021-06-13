package br.com.zupacademy.bcb

import java.time.LocalDateTime

class ResponseRemovePixKey(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)