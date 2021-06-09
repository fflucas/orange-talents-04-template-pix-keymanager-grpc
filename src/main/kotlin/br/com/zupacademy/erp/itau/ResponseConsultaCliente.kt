package br.com.zupacademy.erp.itau

data class ResponseConsultaCliente(
    val id: String? = null,
    val nome: String? = null,
    val cpf: String? = null,
    val instituicao: Instituicao? = null
){
    data class Instituicao(
        val nome: String? = null,
        val ispb: String? = null
    )
}