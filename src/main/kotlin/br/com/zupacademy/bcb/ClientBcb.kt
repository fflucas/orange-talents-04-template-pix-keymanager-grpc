package br.com.zupacademy.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client(value = "\${bcb.host}")
interface ClientBcb {

    @Post(value = "\${bcb.pix.keys}", processes = [MediaType.APPLICATION_XML])
    fun createPixKey(@Body requestCreatePixKey: RequestCreatePixKey): HttpResponse<ResponseCreatePixKey>

    @Delete(value = "\${bcb.pix.keys}/{id}", processes = [MediaType.APPLICATION_XML])
    fun removePixKey(@PathVariable id: String, @Body requestRemovePixKey: RequestRemovePixKey): HttpResponse<ResponseRemovePixKey>
}