package br.com.zupacademy.key.remove

import br.com.zupacademy.RemoveKeyServiceGrpc
import br.com.zupacademy.RequestDeleteKey
import br.com.zupacademy.ResponseDeleteKey
import br.com.zupacademy.util.errors.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RemoveKeyGrpcEndpoint(
    @field:Inject val serviceRemoveKey: ServiceRemoveKey,
) : RemoveKeyServiceGrpc.RemoveKeyServiceImplBase() {

    override fun deleteKey(request: RequestDeleteKey?, responseObserver: StreamObserver<ResponseDeleteKey>?) {
        val idPix = request?.idPix
        val idOwner = request?.idOwner

        val removeResponse = serviceRemoveKey.remove(idPix, idOwner)

        responseObserver?.onNext(
            ResponseDeleteKey.newBuilder()
                .setMessage(removeResponse)
                .build()
        )
        responseObserver?.onCompleted()
    }
}