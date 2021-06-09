package br.com.zupacademy.util.errors

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor

class ErrorInterceptor: ServerInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?
    ): ServerCall.Listener<ReqT> {
        TODO("Not yet implemented")
    }
}