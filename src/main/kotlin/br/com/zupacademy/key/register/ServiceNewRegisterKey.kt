package br.com.zupacademy.key.register

import br.com.zupacademy.AccountType
import br.com.zupacademy.bcb.*
import br.com.zupacademy.erp.itau.ClientErpItau
import br.com.zupacademy.key.RepositoryPix
import br.com.zupacademy.util.exceptions.ExistingPixKeyException
import io.micronaut.http.HttpStatus
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
    @field:Inject val clientErpItau: ClientErpItau,
    @field:Inject val clientBcb: ClientBcb,
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun register(@Valid request: RequestNewRegisterKey): UUID {
        logger.info("Nova solicitação de registro de chave pix do tipo ${request.keyType} para o usuário ${request.idOwner}")
        // checa se valor da chave ja existe
        if(request.keyValue != null){
            if(repositoryPix.existsByKeyValue(request.keyValue)){
                throw ExistingPixKeyException("Chave pix ${request.keyValue} já foi cadastrada")
            }
        }

        // busca dados da conta
        val responseClientAcc = clientErpItau.consulta_contas(request.idOwner!!, request.accType!!.name)
        val clienteAcc = responseClientAcc?.convertToAssociateAccount() ?: throw IllegalStateException("Cliente ${request.idOwner} não encontrado no ERP Itau")

        // integração com bcb
        val requestCreatePixKey = RequestCreatePixKey(
            keyType = request.keyType!!.name,
            key = request.keyValue,
            bankAccount = BankAccount(
                branch = responseClientAcc.agencia,
                accountNumber = responseClientAcc.numero,
                accountType = BankAccountType.convertFromAccountType(responseClientAcc.tipo)
            ),
            owner = Owner(
                name = responseClientAcc.titular.nome,
                taxIdNumber = responseClientAcc.titular.cpf
            )
        )
        val response = clientBcb.createPixKey(requestCreatePixKey)
        if(response.status != HttpStatus.CREATED){
            throw IllegalStateException("BCB não conseguiu cadastrar a chave pix")
        }
        val responseCreatePixKey = response.body()!!

        // salva chave
        val pix = request.convertToEntity(clienteAcc, responseCreatePixKey)
        repositoryPix.save(pix)

        logger.info("Chave pix do tipo ${pix.keyType} com o valor ${pix.keyValue} registrada com sucesso")
        return pix.id!!
    }
}