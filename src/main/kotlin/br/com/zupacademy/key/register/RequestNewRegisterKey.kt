package br.com.zupacademy.key.register

import br.com.zupacademy.AccountType
import br.com.zupacademy.bcb.ResponseCreateAndFindPixKey
import br.com.zupacademy.key.AssociatedAcc
import br.com.zupacademy.key.KeyType
import br.com.zupacademy.key.Pix
import br.com.zupacademy.key.register.validators.ValidPixKey
import br.com.zupacademy.util.validators.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class RequestNewRegisterKey(

    @ValidUUID
    @field:NotBlank
    val idOwner: String?,

    @field:NotNull
    val keyType: KeyType?,

    @field:Size(max = 77)
    val keyValue: String?,

    @field:NotNull
    val accType: AccountType?
){
    fun convertToEntity(associatedAcc: AssociatedAcc, responseCreateAndFindPixKey: ResponseCreateAndFindPixKey): Pix{
        return Pix(
            idOwner = this.idOwner!!,
            keyType = this.keyType!!,
            keyValue = responseCreateAndFindPixKey.key,
            accType = this.accType!!,
            associatedAcc = associatedAcc,
            createdAt = responseCreateAndFindPixKey.createdAt
        )
    }
}
