package br.com.zupacademy.bcb

import br.com.zupacademy.erp.itau.ISPB_ITAU

data class BankAccount(
    val branch: String,
    val accountNumber: String,
){
    val participant: String = ISPB_ITAU
    val accountType: String = "CACC"
}
