package br.com.zupacademy.erp.itau

import br.com.zupacademy.AccountType
import br.com.zupacademy.key.AssociatedAcc

data class ResponseConsultaConta(
    val tipo: AccountType,
    val instituicao: ResponseInstituicao,
    val agencia: String,
    val numero: String,
    val titular: ResponseTitular
){
    fun convertToAssociateAccount(): AssociatedAcc {
        return AssociatedAcc(
            bank = instituicao.nome,
            owner = titular.nome,
            cpf = titular.cpf,
            agency = agencia,
            account = numero
        )
    }
}
