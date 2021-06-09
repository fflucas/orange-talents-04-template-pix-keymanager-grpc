package br.com.zupacademy.key.register

import br.com.zupacademy.erp.itau.ClientErpItau
import br.com.zupacademy.key.RepositoryPix
import br.com.zupacademy.util.exceptions.ExistingPixKeyException
import io.micronaut.validation.Validated
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class ServiceNewRegisterKey(
    @field:Inject val repositoryPix: RepositoryPix,
    @field:Inject val clientErpItau: ClientErpItau
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun register(@Valid request: RequestNewRegisterKey): UUID {
        logger.info("Nova solicitação de registro de chave pix do tipo ${request.keyType} com o valor ${request.keyValue}")
        // checa se valor da chave ja existe
        if(request.keyValue != null){
            if(repositoryPix.existsByKeyValue(request.keyValue)){
                throw ExistingPixKeyException("Chave pix ${request.keyValue} já foi cadastrada")
            }
        }

        // busca dados da conta
        val responseClientAcc = clientErpItau.consulta_contas(request.idOwner!!, request.accType!!.name)
        val clienteAcc = responseClientAcc?.convertToEntity() ?: throw IllegalStateException("Cliente ${request.idOwner} não encontrado no ERP Itau")

        // salva chave
        val pix = request.convertToEntity(clienteAcc)
        repositoryPix.save(pix)

        logger.info("Chave pix do tipo ${pix.keyType} com o valor ${pix.keyValue} registrada com sucesso")
        return pix.id!!
    }
}