package br.com.zupacademy.key.register.validators

import br.com.zupacademy.key.register.RequestNewRegisterKey
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [ValidPixKeyValidator::class])
@Retention(RUNTIME)
@Target(CLASS, TYPE)
annotation class ValidPixKey(
    val message: String = "chave pix inválida (\${validatedValue.keyType})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidPixKeyValidator: ConstraintValidator<ValidPixKey, RequestNewRegisterKey> {

    override fun isValid(
        value: RequestNewRegisterKey?,
        context: ConstraintValidatorContext?
    ): Boolean {
        if(value?.keyType == null){
            return false
        }

        return value.keyType.isValidKey(value.keyValue)
    }
}
