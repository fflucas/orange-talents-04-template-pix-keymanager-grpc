package br.com.zupacademy.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client(value = "\${bcb.host}")
interface ClientBcb {

    @Post(value = "\${bcb.pix.keys}", processes = [MediaType.APPLICATION_XML])
    fun createPixKey(@Body requestCreatePixKey: RequestCreatePixKey): HttpResponse<ResponseCreateAndFindPixKey>

    @Delete(value = "\${bcb.pix.keys}/{id}", processes = [MediaType.APPLICATION_XML])
    fun removePixKey(@PathVariable id: String, @Body requestRemovePixKey: RequestRemovePixKey): HttpResponse<ResponseRemovePixKey>

    @Get(value = "\${bcb.pix.keys}/{id}", processes = [MediaType.APPLICATION_XML])
    fun findByKey(@PathVariable id: String): HttpResponse<ResponseCreateAndFindPixKey>
}