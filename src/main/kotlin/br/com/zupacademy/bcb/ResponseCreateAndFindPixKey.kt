package br.com.zupacademy.bcb

import br.com.zupacademy.AccountType
import br.com.zupacademy.key.AssociatedAcc
import br.com.zupacademy.key.KeyType
import br.com.zupacademy.key.show.PixKeyDto
import java.time.LocalDateTime

data class ResponseCreateAndFindPixKey(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {
    fun toModel(): PixKeyDto {
        return PixKeyDto(
            keyType = keyType,
            keyValue = key,
            associatedAcc = AssociatedAcc(
                bank = "ITAÚ UNIBANCO S.A.", // ponto de melhoria, pelo ispb buscar nome na lista
                owner = owner.name,
                cpf = owner.taxIdNumber,
                agency = bankAccount.branch,
                account = bankAccount.accountNumber
            ),
            accType = when(bankAccount.accountType){
                BankAccountType.CACC -> AccountType.CONTA_CORRENTE
                BankAccountType.SVGS -> AccountType.CONTA_POUPANCA
            },
            createAt = createdAt
        )
    }
}
