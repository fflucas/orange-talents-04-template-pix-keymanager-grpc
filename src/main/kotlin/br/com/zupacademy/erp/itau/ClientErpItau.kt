package br.com.zupacademy.erp.itau

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.client.annotation.Client

@Client(value = "\${erp.itau.host}")
interface ClientErpItau {

    @Get(value = "\${erp.itau.consulta.clientes}/{id}")
    fun consulta_cliente(@PathVariable id: String): ResponseConsultaCliente?
}