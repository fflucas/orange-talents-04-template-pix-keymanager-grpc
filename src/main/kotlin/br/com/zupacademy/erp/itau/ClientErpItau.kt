package br.com.zupacademy.erp.itau

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "\${erp.itau.host}")
interface ClientErpItau {

    @Get(value = "\${erp.itau.consulta.clientes}/{id}/contas{?tipo}")
    fun consultaContas(@PathVariable id: String, @QueryValue tipo: String): ResponseConsultaConta?
}