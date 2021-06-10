package br.com.zupacademy.erp.itau

data class ResponseConsultaCliente(
    val id: String,
    val nome: String,
    val cpf: String,
    val responseInstituicao: ResponseInstituicao
){

}