package br.com.zupacademy.key.register

import br.com.zupacademy.AccountType.UNKNOWN_ACC_TYPE
import br.com.zupacademy.KeyType.UNKNOWN_KEY_TYPE
import br.com.zupacademy.RegisterKeyServiceGrpc
import br.com.zupacademy.RequestNewKey
import br.com.zupacademy.ResponseNewKey
import br.com.zupacademy.key.KeyType
import br.com.zupacademy.util.errors.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RegisterKeyGrpcEndpoint(
    @field:Inject val service: ServiceNewRegisterKey,
) : RegisterKeyServiceGrpc.RegisterKeyServiceImplBase() {

    override fun registerKey(request: RequestNewKey, responseObserver: StreamObserver<ResponseNewKey>?) {
        val pixId = service.register(
            request.convertToDto()
        )
        responseObserver?.onNext(
            ResponseNewKey.newBuilder()
                .setPixId(pixId.toString())
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