package br.com.zupacademy.key.remove

import br.com.zupacademy.AccountType
import br.com.zupacademy.RemoveKeyServiceGrpc
import br.com.zupacademy.RequestDeleteKey
import br.com.zupacademy.bcb.ClientBcb
import br.com.zupacademy.bcb.RequestRemovePixKey
import br.com.zupacademy.bcb.ResponseRemovePixKey
import br.com.zupacademy.erp.itau.ISPB_ITAU
import br.com.zupacademy.erp.itau.ResponseConsultaConta
import br.com.zupacademy.erp.itau.ResponseInstituicao
import br.com.zupacademy.erp.itau.ResponseTitular
import br.com.zupacademy.key.KeyType
import br.com.zupacademy.key.Pix
import br.com.zupacademy.key.RepositoryPix
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveKeyGrpcEndpointTest(
    private val repositoryPix: RepositoryPix,
    private val grpcClient: RemoveKeyServiceGrpc.RemoveKeyServiceBlockingStub
) {

    @Inject
    lateinit var clientBcb: ClientBcb

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
        val responseRemovePixKey = responseRemovePixKey(pixKey.keyValue)
        `when`(clientBcb.removePixKey(
            id = pixKey.keyValue,
            requestRemovePixKey(pixKey.keyValue)
        ))
            .thenReturn(HttpResponse.ok(responseRemovePixKey))

        val requestDeleteKey = RequestDeleteKey.newBuilder()
            .setIdPix(pixKey.id.toString())
            .setIdOwner(pixKey.idOwner)
            .build()

        //acao
        val response = grpcClient.deleteKey(requestDeleteKey)

        //resultado
        assertEquals("Chave pix removida com sucesso em ${responseRemovePixKey.deletedAt}", response.message)
        assertTrue(repositoryPix.findById(pixKey.id!!).isEmpty)
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

    @MockBean(ClientBcb::class)
    fun mockBcbClient(): ClientBcb {
        return Mockito.mock(ClientBcb::class.java)
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
                id = CLIENT_ID.toString(),
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
            associatedAcc = responseConsultaConta().convertToAssociateAccount(),
            createdAt = LocalDateTime.now()
        )
    }

    private fun requestRemovePixKey(key: String): RequestRemovePixKey{
        return RequestRemovePixKey(
            key,
            participant = ISPB_ITAU
        )
    }

    private fun responseRemovePixKey(key: String): ResponseRemovePixKey {
        return ResponseRemovePixKey(
            key,
            participant = ISPB_ITAU,
            deletedAt = LocalDateTime.now()
        )
    }
}