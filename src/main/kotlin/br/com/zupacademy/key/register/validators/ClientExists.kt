package br.com.zupacademy.key.register.validators

import javax.validation.Constraint

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ClientExistsValidator::class])
annotation class ClientExists(
    val message: String = "deve ser um cliente existente"
)