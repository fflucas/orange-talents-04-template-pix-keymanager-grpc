package br.com.zupacademy.key.show

import br.com.zupacademy.RequestShowKey
import br.com.zupacademy.ResponseShowKey
import br.com.zupacademy.ShowKeyServiceGrpc
import br.com.zupacademy.bcb.ClientBcb
import br.com.zupacademy.key.RepositoryPix
import br.com.zupacademy.util.errors.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ShowKeyGrpcEndpoint(
    @field:Inject val repositoryPix: RepositoryPix,
    @Inject val clientBcb: ClientBcb,
): ShowKeyServiceGrpc.ShowKeyServiceImplBase() {

    override fun showKey(request: RequestShowKey, responseObserver: StreamObserver<ResponseShowKey>?) {
        val filter: Filter = request.toModel()
        val pixKeyDto: PixKeyDto = filter.call(repositoryPix, clientBcb)

        responseObserver?.onNext(LoadResponseShowKeyFromDto.convert(pixKeyDto))
        responseObserver?.onCompleted()
    }
}