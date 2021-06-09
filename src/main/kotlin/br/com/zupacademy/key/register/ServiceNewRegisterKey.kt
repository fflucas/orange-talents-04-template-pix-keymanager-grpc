package br.com.zupacademy.key.register

import br.com.zupacademy.key.Pix
import br.com.zupacademy.key.PixKeyType
import br.com.zupacademy.key.RepositoryPix
import io.micronaut.validation.Validated
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class ServiceNewRegisterKey(
    @field:Inject val repositoryPix: RepositoryPix,
) {
    private val logger: Logger = LoggerFactory.getLogger(ServiceNewRegisterKey::class.java)

    fun save(@Valid request: RequestNewRegisterKey): String {
        logger.info("Tentando registrar nova chave pix do tipo ${request.keyType} para titular ${request.idOwner}")
        validateRequestKey(request)
        val pix = request.convertToEntity()
        repositoryPix.save(pix)
        logger.info("Nova chave pix ${pix.keyValue} registrada para titular ${pix.owner}")
        return pix.id
    }

    private fun validateRequestKey(request: RequestNewRegisterKey) {
        // o formato da chave
        val isValidKey = request.keyType!!.isValidKey(request.keyValue)
        if(!isValidKey){
            throw Exception("o formato da chave é inválido")
        }
        if(isValidKey && request.keyType != PixKeyType.RANDOM){
            // se chave e unica
            val findById: Optional<Pix> = repositoryPix.findByKeyValue(request.keyValue)
            if(findById.isPresent){
                throw Exception("o valor da chave não é único")
            }
        }
    }
}