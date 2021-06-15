package br.com.zupacademy.key.show

import br.com.zupacademy.KeyType
import br.com.zupacademy.ResponseShowKey
import com.google.protobuf.Timestamp
import java.time.ZoneId

class LoadResponseShowKeyFromDto {

    companion object{
        fun convert(pixKeyDto: PixKeyDto): ResponseShowKey{
            return with(pixKeyDto){
                ResponseShowKey.newBuilder()
                    .setIdPix(pixId?.toString() ?: "")
                    .setIdOwner(idOwner ?: "")
                    .setKeyType(KeyType.valueOf(keyType.name))
                    .setKeyValue(keyValue)
                    .setName(associatedAcc.owner)
                    .setCpf(associatedAcc.cpf)
                    .setAccInfo(ResponseShowKey.AccountInfo.newBuilder()
                        .setBank(associatedAcc.bank)
                        .setAgency(associatedAcc.agency)
                        .setAccount(associatedAcc.account)
                        .setAccType(accType)
                        .build()
                    )
                    .setCreatedAt(createAt.let {
                        val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                        Timestamp.newBuilder()
                            .setSeconds(createdAt.epochSecond)
                            .setNanos(createdAt.nano)
                            .build()
                        }
                    )
                    .build()
            }
        }
    }
}
