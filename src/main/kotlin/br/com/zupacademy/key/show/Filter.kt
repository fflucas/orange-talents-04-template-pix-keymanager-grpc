package br.com.zupacademy.key.show

import br.com.zupacademy.bcb.ClientBcb
import br.com.zupacademy.key.RepositoryPix
import br.com.zupacademy.util.exceptions.PixKeyNotFoundException
import br.com.zupacademy.util.validators.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filter {

    abstract fun call (repositoryPix: RepositoryPix, clientBcb: ClientBcb): PixKeyDto

    @Introspected
    data class ByPixId(
        @field:NotBlank @field:ValidUUID val idOwner: String,
        @field:NotBlank @field:ValidUUID val idPix: String
    ): Filter() {
        private fun toUuid(id: String): UUID = UUID.fromString(id)

        override fun call(repositoryPix: RepositoryPix, clientBcb: ClientBcb): PixKeyDto {
            return repositoryPix.findById(toUuid(idPix))
                .filter { it.belongsTo(idOwner) }
                .map(PixKeyDto::of)
                .orElseThrow { PixKeyNotFoundException("Chave pix $idPix não encontrada!") }
        }
    }

    @Introspected
    data class ByPixKey(
        @field:NotBlank @field:Size(max=77) val pixKey: String
    ): Filter() {
        private val logger = LoggerFactory.getLogger(this::class.java)

        override fun call(repositoryPix: RepositoryPix, clientBcb: ClientBcb): PixKeyDto {
            return repositoryPix.findByKeyValue(pixKey)
                .map(PixKeyDto::of)
                .orElseGet {
                    logger.info("Consultando chave pix $pixKey no sistema BCB")

                    val response = clientBcb.findByKey(pixKey)
                    when(response.status){
                        HttpStatus.OK -> response.body()?.toModel()
                        else -> throw PixKeyNotFoundException("Chave pix $pixKey não encontrada")
                    }
                }
        }
    }

    @Introspected
    object Invalid : Filter() {
        override fun call(repositoryPix: RepositoryPix, clientBcb: ClientBcb): PixKeyDto {
            throw IllegalArgumentException("Chave pix inválida ou não informada")
        }
    }

}
