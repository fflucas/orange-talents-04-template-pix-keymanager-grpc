package br.com.zupacademy.key.register

import br.com.zupacademy.*
import br.com.zupacademy.AccountType.*
import br.com.zupacademy.KeyType.*
import br.com.zupacademy.key.KeyType
import br.com.zupacademy.util.errors.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RegisterKeyGrpcEndpoint(
    @field:Inject val service: ServiceNewRegisterKey,
) : KeyManagerServiceGrpc.KeyManagerServiceImplBase() {

    override fun registerKey(request: RequestNewKey, responseObserver: StreamObserver<ResponseNewKey>?) {
        val pixId = service.register(
            request.convertToDto()
        )
        responseObserver?.onNext(
            ResponseNewKey.newBuilder()
                .setPixId(pixId)
                .build()
        )
        responseObserver?.onCompleted()
    }

    // extension method
    private fun RequestNewKey.convertToDto() = RequestNewRegisterKey(
        idOwner = idOwner,
        keyType = when(keyType){
            UNKNOWN_KEY_TYPE -> null
            else -> KeyType.valueOf(this.keyType.name)
        },
        keyValue = keyValue,
        accType = when(accType){
            UNKNOWN_ACC_TYPE -> null
            else -> accType
        }
    )

}