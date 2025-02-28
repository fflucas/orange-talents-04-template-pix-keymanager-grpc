package br.com.zupacademy.util.errors

import br.com.zupacademy.util.errors.ExceptionHandler.*
import br.com.zupacademy.util.exceptions.ExistingPixKeyException
import br.com.zupacademy.util.exceptions.PixKeyNotFoundException
import io.grpc.Status
import io.micronaut.http.client.exceptions.HttpClientException
import javax.validation.ConstraintViolationException

class DefaultExceptionHandler : ExceptionHandler<Exception> {

    // mapeia as exceções devolvendo o status grpc adequado
    override fun handle(e: Exception): StatusWithDetails {
        val status = when(e) {
            is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message)
            is ConstraintViolationException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            is ExistingPixKeyException -> Status.ALREADY_EXISTS.withDescription(e.message)
            is PixKeyNotFoundException -> Status.NOT_FOUND.withDescription(e.message)
            is HttpClientException -> Status.OUT_OF_RANGE.withDescription(e.message)
            else -> Status.UNKNOWN.withDescription(e.message)
        }
        return StatusWithDetails(status.withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return true
    }
}
