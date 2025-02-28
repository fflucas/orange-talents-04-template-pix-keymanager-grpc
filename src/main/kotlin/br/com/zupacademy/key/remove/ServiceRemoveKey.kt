package br.com.zupacademy.key.remove

import br.com.zupacademy.bcb.ClientBcb
import br.com.zupacademy.bcb.RequestRemovePixKey
import br.com.zupacademy.erp.itau.ISPB_ITAU
import br.com.zupacademy.key.RepositoryPix
import br.com.zupacademy.util.exceptions.PixKeyNotFoundException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotBlank

@Singleton
@Validated
class ServiceRemoveKey(
    @field:Inject val repositoryPix: RepositoryPix,
    @field:Inject val clientBcb: ClientBcb,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun remove(
        @NotBlank idPix: String?,
        @NotBlank idOwner: String?,
    ): String {
        //buscar chave pix e lançar erro caso nao encontrada
        val optionalPix = repositoryPix.findByIdAndIdOwner(
            UUID.fromString(idPix!!),
            UUID.fromString(idOwner!!).toString()
        )
        if(optionalPix.isEmpty){
            throw PixKeyNotFoundException(message = "Chave pix não encontrada")
        }
        val pix = optionalPix.get()
        logger.info("Nova solicitação para remoção de chave pix com id ${pix.id}")

        //remover chave do bcb
        val responseRemovePixKey = clientBcb.removePixKey(
            pix.keyValue,
            RequestRemovePixKey(
                key = pix.keyValue,
                participant = ISPB_ITAU
            )
        )
        if(responseRemovePixKey.status != HttpStatus.OK){
            throw PixKeyNotFoundException("Chave pix não encontrada no sistema BCB")
        }
        val localDateTime = responseRemovePixKey.body()?.deletedAt

        //remover chave local
        repositoryPix.deleteById(pix.id!!)
        logger.info("Chave pix com id ${pix.id} removida com sucesso em $localDateTime")
        return "Chave pix removida com sucesso em $localDateTime"
    }
}