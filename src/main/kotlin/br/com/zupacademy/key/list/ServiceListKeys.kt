package br.com.zupacademy.key.list

import br.com.zupacademy.bcb.ClientBcb
import br.com.zupacademy.key.Pix
import br.com.zupacademy.key.RepositoryPix
import br.com.zupacademy.util.validators.ValidUUID
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotBlank

@Singleton
@Validated
class ServiceListKeys(
    @field:Inject val repositoryPix: RepositoryPix,
    @field:Inject val clientBcb: ClientBcb,
) {
    fun list(
        @NotBlank @ValidUUID idOwner: String
    ): List<Pix> {
        // validar se o id recebido é uuid valido - OK
        // buscar chaves pelo idOwner
        val ownerKeys: List<Pix> = repositoryPix.findAllByIdOwner(idOwner)
        // para cada chave consultar se existe no bcb
        // somente se existir entra na lista de resposta
        return ownerKeys.filter { pix ->
            clientBcb.findByKey(pix.keyValue).status == HttpStatus.OK
        }
    }
}