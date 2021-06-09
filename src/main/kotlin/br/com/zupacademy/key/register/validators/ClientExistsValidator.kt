package br.com.zupacademy.key.register.validators

import br.com.zupacademy.erp.itau.ClientErpItau
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientExistsValidator(
    @field:Inject val clientErpItau: ClientErpItau
) : ConstraintValidator<ClientExists, String> {

    override fun isValid(
        value: String,
        annotationMetadata: AnnotationValue<ClientExists>,
        context: ConstraintValidatorContext
    ): Boolean {

        return with(clientErpItau) {
            try{
                when(consulta_cliente(id = value)){
                    null -> false
                    else -> true
                }
            }catch (e: HttpClientException){
                // alterar exceção
                throw Exception("Servidor ERP Itau indisponível", e)
            }
        }
    }
}