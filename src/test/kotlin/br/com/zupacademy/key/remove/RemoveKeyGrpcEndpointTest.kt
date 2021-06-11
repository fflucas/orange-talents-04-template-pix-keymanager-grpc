package br.com.zupacademy.key.remove

import br.com.zupacademy.AccountType
import br.com.zupacademy.RemoveKeyServiceGrpc
import br.com.zupacademy.RequestDeleteKey
import br.com.zupacademy.erp.itau.ResponseConsultaConta
import br.com.zupacademy.erp.itau.ResponseInstituicao
import br.com.zupacademy.erp.itau.ResponseTitular
import br.com.zupacademy.key.KeyType
import br.com.zupacademy.key.Pix
import br.com.zupacademy.key.RepositoryPix
import br.com.zupacademy.key.register.RegisterKeyGrpcEndpointTest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveKeyGrpcEndpointTest(
    private val repositoryPix: RepositoryPix,
    private val grpcClient: RemoveKeyServiceGrpc.RemoveKeyServiceBlockingStub
) {

    companion object{
        val CLIENT_ID: UUID = UUID.randomUUID()
    }

    private lateinit var pixKey: Pix

    @BeforeEach
    fun setUp() {
        pixKey = repositoryPix.save(newPixKey(
            ownerId = CLIENT_ID.toString(),
        ))
    }

    @AfterEach
    fun tearDown(){
        repositoryPix.deleteAll()
    }

    @Test
    fun `it should not be possible to remove a pix key if the pix id is incorrect`(){
        //cenario
        val requestDeleteKey = RequestDeleteKey.newBuilder()
            .setIdPix(UUID.randomUUID().toString())
            .setIdOwner(pixKey.idOwner)
            .build()

        //acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.deleteKey(requestDeleteKey)
        }

        //resultado
        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada", status.description)
        }
    }

    @Test
    fun `it should not be possible to remove a pix key if the owner id is incorrect`(){
        //cenario
        val requestDeleteKey = RequestDeleteKey.newBuilder()
            .setIdPix(pixKey.id.toString())
            .setIdOwner(UUID.randomUUID().toString())
            .build()

        //acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.deleteKey(requestDeleteKey)
        }

        //resultado
        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada", status.description)
        }
    }

    @Test
    fun `it should be possible to remove a pix key`(){
        //cenario
        val requestDeleteKey = RequestDeleteKey.newBuilder()
            .setIdPix(pixKey.id.toString())
            .setIdOwner(pixKey.idOwner)
            .build()

        //acao
        val response = grpcClient.deleteKey(requestDeleteKey)


        //resultado
        assertEquals("Chave pix removida com sucesso", response.message)
        assertTrue(repositoryPix.findById(pixKey.id).isEmpty)
    }

    @Test
    fun `it should not be possible to remove a pix key if the request parameters are missing`(){
        //cenario
        val requestDeleteKey = RequestDeleteKey.newBuilder().build()

        //acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.deleteKey(requestDeleteKey)
        }

        //resultado
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }


    @Factory
    class Client{
        @Singleton
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ): RemoveKeyServiceGrpc.RemoveKeyServiceBlockingStub {
            return RemoveKeyServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun responseConsultaConta(): ResponseConsultaConta {
        return ResponseConsultaConta(
            tipo = "CONTA_CORRENTE",
            instituicao = ResponseInstituicao(
                nome = "ITAU UNIBANCO",
                ispb = "1111"
            ),
            agencia = "0001",
            numero = "1213",
            titular = ResponseTitular(
                id = RegisterKeyGrpcEndpointTest.CLIENT_ID.toString(),
                nome = "Fabio Almeida",
                cpf = "11122233345"
            ),
        )
    }

    private fun newPixKey(
        ownerId: String,
    ): Pix {
        return Pix(
            idOwner = ownerId,
            keyType = KeyType.EMAIL,
            keyValue = "fabio@teste.com",
            accType = AccountType.CONTA_CORRENTE,
            associatedAcc = responseConsultaConta().convertToAssociateAccount()
        )
    }
}