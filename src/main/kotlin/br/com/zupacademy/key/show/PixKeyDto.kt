package br.com.zupacademy.key.show

import br.com.zupacademy.AccountType
import br.com.zupacademy.key.AssociatedAcc
import br.com.zupacademy.key.KeyType
import br.com.zupacademy.key.Pix
import java.time.LocalDateTime
import java.util.*

class PixKeyDto(
    val pixId: UUID? = null,
    val idOwner: String? = null,
    val keyType: KeyType,
    val keyValue: String,
    val associatedAcc: AssociatedAcc,
    val accType: AccountType,
    val createAt: LocalDateTime
) {

    companion object{
        fun of(pix: Pix): PixKeyDto{
            return with(pix){
                PixKeyDto(
                    pixId = id,
                    idOwner = idOwner,
                    keyType = keyType,
                    keyValue = keyValue,
                    associatedAcc = associatedAcc,
                    accType = accType,
                    createAt = createdAt
                )
            }
        }
    }
}
