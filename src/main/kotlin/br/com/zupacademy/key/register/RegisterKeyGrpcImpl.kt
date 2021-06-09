package br.com.zupacademy.key.register

import br.com.zupacademy.*
import br.com.zupacademy.key.PixKeyType
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterKeyGrpcImpl(
    @field:Inject val service: ServiceNewRegisterKey,
) : KeyManagerServiceGrpc.KeyManagerServiceImplBase() {

    override fun registerKey(request: RequestNewKey?, responseObserver: StreamObserver<ResponseNewKey>?) {
        val pixId = service.save(
            request!!.convertToDto()
        )
        responseObserver?.onNext(
            ResponseNewKey.newBuilder()
                .setPixId(pixId)
                .build()
        )

        responseObserver?.onCompleted()
    }

    private fun RequestNewKey.convertToDto() = RequestNewRegisterKey(
        idOwner = this.idOwner,
        keyType = when(this.keyType){
            KeyType.CPF -> PixKeyType.CPF
            KeyType.PHONE_NUMBER -> PixKeyType.PHONE_NUMBER
            KeyType.EMAIL -> PixKeyType.EMAIL
            KeyType.RANDOM -> PixKeyType.RANDOM
            else -> null
        },
        keyValue = this.keyValue,
        accType = when(this.accType){
            AccountType.UNKNOWN_ACC_TYPE -> null
            else -> this.accType
        }
    )

}