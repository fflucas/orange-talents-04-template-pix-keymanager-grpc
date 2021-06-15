package br.com.zupacademy.key.list

import br.com.zupacademy.AccountType
import br.com.zupacademy.ListKeysServiceGrpc
import br.com.zupacademy.RequestListKeys
import br.com.zupacademy.bcb.ClientBcb
import br.com.zupacademy.key.AssociatedAcc
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
internal class ListKeysGrpcEndpointTest(
    private val repositoryPix: RepositoryPix,
    private val grpcClient: ListKeysServiceGrpc.ListKeysServiceBlockingStub
) {

    @Inject
    lateinit var clientBcb: ClientBcb

    companion object{
        val CLIENT_ID: String = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setUp() {
        repositoryPix.save(pixKey(keyType = KeyType.CPF, keyValue = "63657520325", idOwner = UUID.randomUUID().toString()))
        repositoryPix.save(pixKey(keyType = KeyType.RANDOM, keyValue = "randomkey-3", idOwner = CLIENT_ID))
    }

    @AfterEach
    fun tearDown() {
        repositoryPix.deleteAll()
    }

    @Test
    fun `it should be possible to list a user's keys only if they are registered both internally and on the BCB client`(){
        //cenario
        val pix = repositoryPix.findByKeyValue("randomkey-3").get()
        `when`(clientBcb.findByKey(id = "randomkey-3"))
            .thenReturn(HttpResponse.ok())

        //acao
        val response = grpcClient.listKeys(
            RequestListKeys.newBuilder()
                .setIdOwner(CLIENT_ID)
                .build()
        )

        //resultado
        with(response){
            assertEquals(1, this.pixKeysCount)
            with(this.getPixKeys(0)){
                assertEquals(pix.id.toString(), this.idPix)
                assertEquals(pix.idOwner, this.idOwner)
                assertEquals(pix.keyValue, this.keyValue)
                assertEquals(pix.accType.name, this.accType.name)
            }
        }
    }

    @Test
    fun `it should not be possible to list a user's keys with an invalid ID`(){
        //cenario
        val request = RequestListKeys.newBuilder()
            .setIdOwner("")
            .build()

        //acao
        val throws = assertThrows<StatusRuntimeException> {
            grpcClient.listKeys(request)
        }

        //resultado
        with(throws){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("list.idOwner: não é um formato válido de UUID, " +
                    "list.idOwner: não pode estar em branco", status.description)
        }
    }

    @Test
    fun `for a user without pix keys it should return an empty list`(){
        //cenario
        val request = RequestListKeys.newBuilder()
            .setIdOwner(UUID.randomUUID().toString())
            .build()

        //cenario
        val response = grpcClient.listKeys(request)

        //resultado
        with(response){
            assertEquals(0, this.pixKeysCount)
        }
    }

    @Factory
    class Client{
        @Singleton
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ): ListKeysServiceGrpc.ListKeysServiceBlockingStub {
            return ListKeysServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ClientBcb::class)
    fun mockClientBcb(): ClientBcb {
        return Mockito.mock(ClientBcb::class.java)
    }

    private fun pixKey(
        keyType: KeyType,
        keyValue: String = UUID.randomUUID().toString(),
        idOwner: String = UUID.randomUUID().toString(),
    ): Pix {
        return Pix(
            idOwner = idOwner,
            keyType = keyType,
            keyValue = keyValue,
            accType = AccountType.CONTA_CORRENTE,
            associatedAcc = AssociatedAcc(
                bank = "UNIBANCO ITAU",
                owner = "Rafael Ponte",
                cpf = "12345678900",
                agency = "1218",
                account = "123456"
            ),
            createdAt = LocalDateTime.now()
        )
    }
}