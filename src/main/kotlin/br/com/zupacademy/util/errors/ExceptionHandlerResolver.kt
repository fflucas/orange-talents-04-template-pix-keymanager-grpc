package br.com.zupacademy.util.errors

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionHandlerResolver(
    @Inject private val handlers: List<ExceptionHandler<Exception>>
) {
    private var defaultHandler: ExceptionHandler<Exception> = DefaultExceptionHandler()

    constructor(handlers: List<ExceptionHandler<Exception>>, defaultHandler: ExceptionHandler<Exception>): this(handlers){
        this.defaultHandler = defaultHandler
    }

    fun resolve(e: Exception): ExceptionHandler<Exception> {
        val foundHandlers = handlers.filter { h -> h.supports(e) } // verifica se para a exceção existe uma tratativa na lista de exceções injetada
        if (foundHandlers.size > 1) // mais de uma tratativa para a mesma exceção deve lançar erro de aplicação
            throw IllegalStateException("Too many handlers supporting the same exception '${e.javaClass.name}': $foundHandlers")
        return foundHandlers.firstOrNull() ?: defaultHandler // retorna a primeira tratativa ou senão uma tratativa padrão.
        //PS.: Aqui sempre é retornado a tratativa padrão já que não foram criados classes individuais (Handler) para cada exceção (Exception)
    }


}