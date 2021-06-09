package br.com.zupacademy.util.errors

import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionHandlerInterceptor(
    @field:Inject private val resolver: ExceptionHandlerResolver
): MethodInterceptor<BindableService, Any?>{

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {
        // intercepta a chamada que utiliza @ErrorHandler
        return try {
            context.proceed()
        }catch (e: Exception){ // em caso de erro no contexto da chamada a exceção é capturada

            logger.error("Handling the exception '${e.javaClass.name}' while processing the call: ${context.targetMethod}", e)

            val handler = resolver.resolve(e) // verifica se para a exceção capturada existe uma tratativa e retorna esta, aqui sempre é retornado a tratativa padrão
            val status = handler.handle(e) // com a tratativa, é invocado o handle para mapear a exceção para um status de erro grpc

            // responde a solicitação com erro e o status adequado para a exceção capturada
            GrpcEndpointArguments(context).response()
                .onError(status.asRuntimeException())

            null
        }
    }

    private class GrpcEndpointArguments(val context: MethodInvocationContext<BindableService, Any?>){
        fun response(): StreamObserver<*> {
            return context.parameterValues[1] as StreamObserver<*>
        }
    }
}