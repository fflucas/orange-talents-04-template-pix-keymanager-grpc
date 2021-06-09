package br.com.zupacademy.key.register

import br.com.zupacademy.AccountType
import br.com.zupacademy.key.Pix
import br.com.zupacademy.key.PixKeyType
import br.com.zupacademy.key.register.validators.ClientExists
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
data class RequestNewRegisterKey(
    @field:NotBlank
    @field:ClientExists
    val idOwner: String,

    @field:NotNull
    val keyType: PixKeyType?,

    @field:NotNull
    @field:Size(max = 77)
    val keyValue: String,

    @field:NotNull
    val accType: AccountType?
){
    fun convertToEntity(): Pix{
        return Pix(
            owner = this.idOwner,
            keyType = this.keyType!!,
            keyValue = this.keyValue,
            accType = this.accType!!
        )
    }
}
