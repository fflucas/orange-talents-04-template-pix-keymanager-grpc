package br.com.zupacademy.key.list

import br.com.zupacademy.KeyType
import br.com.zupacademy.ListKeysServiceGrpc
import br.com.zupacademy.RequestListKeys
import br.com.zupacademy.ResponseListKeys
import br.com.zupacademy.key.Pix
import br.com.zupacademy.util.errors.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ListKeysGrpcEndpoint(
    @field:Inject val serviceListKeys: ServiceListKeys,
): ListKeysServiceGrpc.ListKeysServiceImplBase() {

    override fun listKeys(request: RequestListKeys, responseObserver: StreamObserver<ResponseListKeys>?) {
        val listOfKeys = serviceListKeys.list(request.idOwner)

        val listOfResponseKeys = mutableListOf<ResponseListKeys.Key>()
        listOfKeys.forEach {
            listOfResponseKeys.add(
                convertToResponseList(it)
            )
        }

        val builder = ResponseListKeys.newBuilder().addAllPixKeys(listOfResponseKeys)
        responseObserver?.onNext(
            builder.build()
        )
        responseObserver?.onCompleted()
    }

    private fun convertToResponseList(pix: Pix): ResponseListKeys.Key{
         return with(pix){
            ResponseListKeys.Key.newBuilder()
                .setIdPix(id.toString())
                .setIdOwner(idOwner)
                .setKeyType(KeyType.valueOf(keyType.name))
                .setKeyValue(keyValue)
                .setAccType(accType)
                .setCreatedAt(createdAt.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                }
                )
                .build()
        }
    }
}