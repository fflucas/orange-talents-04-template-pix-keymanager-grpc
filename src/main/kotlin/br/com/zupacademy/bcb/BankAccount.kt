package br.com.zupacademy.bcb

import br.com.zupacademy.AccountType
import br.com.zupacademy.erp.itau.ISPB_ITAU
import java.lang.IllegalStateException

data class BankAccount(
    val branch: String,
    val accountNumber: String,
    val accountType: BankAccountType
){
    val participant: String = ISPB_ITAU
}

enum class BankAccountType{
    CACC,
    SVGS;

    companion object {
        fun convertFromAccountType(accountType: AccountType): BankAccountType {
            return when(accountType){
                AccountType.CONTA_CORRENTE -> CACC
                AccountType.CONTA_POUPANCA -> SVGS
                else -> throw IllegalStateException("Tipo de conta não é valida")
            }
        }
    }
}


