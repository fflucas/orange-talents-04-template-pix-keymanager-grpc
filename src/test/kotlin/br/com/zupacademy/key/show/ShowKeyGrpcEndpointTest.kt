package br.com.zupacademy.key.show

import br.com.zupacademy.AccountType
import br.com.zupacademy.RequestShowKey
import br.com.zupacademy.ShowKeyServiceGrpc
import br.com.zupacademy.bcb.*
import br.com.zupacademy.key.AssociatedAcc
import br.com.zupacademy.key.KeyType
import br.com.zupacademy.key.KeyType.*
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
internal class ShowKeyGrpcEndpointTest(
    private val repositoryPix: RepositoryPix,
    private val grpcClient: ShowKeyServiceGrpc.ShowKeyServiceBlockingStub,
) {

    @Inject
    lateinit var clientBcb: ClientBcb

    companion object{
        val CLIENT_ID: String = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setUp() {
        repositoryPix.save(pixKey(keyType = EMAIL, keyValue = "rafael.ponte@zup.com.br", idOwner = CLIENT_ID))
        repositoryPix.save(pixKey(keyType = CPF, keyValue = "63657520325", idOwner = UUID.randomUUID().toString()))
        repositoryPix.save(pixKey(keyType = RANDOM, keyValue = "randomkey-3", idOwner = CLIENT_ID))
        repositoryPix.save(pixKey(keyType = PHONE_NUMBER, keyValue = "+551155554321", idOwner = CLIENT_ID))
    }

    @AfterEach
    fun tearDown() {
        repositoryPix.deleteAll()
    }

    @Test
    fun `it should be possible to show a pix by idPix and idOwner`(){
        // cenario
        val pix = repositoryPix.findByKeyValue("63657520325").get()

        // acao
        val response = grpcClient.showKey(
            RequestShowKey.newBuilder()
                .setPixId(
                    RequestShowKey.FilterByPixId.newBuilder()
                        .setIdPix(pix.id.toString())
                        .setIdOwner(pix.idOwner)
                        .build()
                )
                .build()
        )

        // resultado
        with(response){
            assertEquals(pix.id.toString(), this.idPix)
            assertEquals(pix.idOwner, this.idOwner)
            assertEquals(pix.keyType.name, this.keyType.name)
            assertEquals(pix.keyValue, this.keyValue)
            assertEquals(pix.associatedAcc.owner, this.name)
            assertEquals(pix.associatedAcc.cpf, this.cpf)
        }
    }

    @Test
    fun `it should not be possible to show a pix by idPix and idOwner when given filter is invalid`(){
        //cenario
        val request = RequestShowKey.newBuilder()
            .setPixId(
                RequestShowKey.FilterByPixId.newBuilder()
                    .setIdPix("")
                    .setIdOwner("")
                    .build()
            ).build()

        // acao
        val throws = assertThrows<StatusRuntimeException> {
            grpcClient.showKey(request)
        }

        with(throws){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            /*assertThat(violations(), containsInAnyOrder(
                Pair("idOwner", "must not be blank"),
                Pair("idPix", "must not be blank"),
                Pair("idOwner", "idOwner deve ser UUID válido"),
                Pair("idPix", "idPix deve ser UUID válido"),
            ))*/
        }
    }

    @Test
    fun `it should not be possible to show a pix by idPix and idOwner when the register does not exists`(){
        //cenario
        val randomIdPix = UUID.randomUUID().toString()
        val randomIdOwner = UUID.randomUUID().toString()

        //acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.showKey(RequestShowKey.newBuilder()
                .setPixId(RequestShowKey.FilterByPixId.newBuilder()
                    .setIdPix(randomIdPix)
                    .setIdOwner(randomIdOwner)
                    .build()
                )
                .build()
            )
        }

        //resultado
        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix $randomIdPix não encontrada!", status.description)
        }
    }

    @Test
    fun `it should be possible to show a pix by key value when the register exists internally`(){
        //cenario
        val pix = repositoryPix.findByKeyValue("63657520325").get()

        //acao
        val response = grpcClient.showKey(
            RequestShowKey.newBuilder()
                .setPixKey(pix.keyValue)
                .build()
        )

        //resultado
        with(response){
            assertEquals(pix.id.toString(), this.idPix)
            assertEquals(pix.idOwner, this.idOwner)
            assertEquals(pix.keyValue, this.keyValue)
            assertEquals(pix.associatedAcc.owner, this.name)
            assertEquals(pix.associatedAcc.cpf, this.cpf)
        }
    }

    @Test
    fun `it should be possible to show a pix by key value when the register does not exist internally but exists on BCB client`(){
        //cenario
        val responseFindPixKey = responseFindPixKey()
        `when`(clientBcb.findByKey(id = "user.from.another.bank@santander.com.br"))
            .thenReturn(HttpResponse.ok(responseFindPixKey))

        //acao
        val response = grpcClient.showKey(
            RequestShowKey.newBuilder()
                .setPixKey("user.from.another.bank@santander.com.br")
                .build()
        )

        //resultado
        with(response){
            assertEquals("", this.idPix)
            assertEquals("", this.idOwner)
            assertEquals(responseFindPixKey.key, this.keyValue)
            assertEquals(responseFindPixKey.keyType.name, this.keyType.name)
        }
    }

    @Test
    fun `it should not be possible to show a pix by key value when the register does not exist internally or in the BCB client`(){
        //cenario
        `when`(clientBcb.findByKey(id = "not.existing.user@santander.com.br"))
            .thenReturn(HttpResponse.notFound())

        //acao
        val throws = assertThrows<StatusRuntimeException> {
            grpcClient.showKey(
                RequestShowKey.newBuilder()
                    .setPixKey("not.existing.user@santander.com.br")
                    .build()
            )
        }

        //resultado
        with(throws){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix not.existing.user@santander.com.br não encontrada", status.description)
        }
    }

    @Test
    fun `it should not be possible to show a pix by key value when given filter is invalid`(){
        //cenario
        val requestShowKey = RequestShowKey.newBuilder()
            .setPixKey("")
            .build()

        //acao
        val throws = assertThrows<StatusRuntimeException> {
            grpcClient.showKey(requestShowKey)
        }

        //resultado
        with(throws){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("pixKey: não pode estar em branco", status.description)
//            assertThat(violations(), containsInAnyOrder(
//                Pair("pixKey", "não pode estar em branco")
//            ))
        }
    }

    @Test
    fun `it should not be possible to show pix when filter is not set`(){
        //cenario
        val requestShowKey = RequestShowKey.newBuilder().build()

        //acao
        val throws = assertThrows<StatusRuntimeException> {
            grpcClient.showKey(requestShowKey)
        }

        //resultado
        with(throws){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave pix inválida ou não informada", status.description)
        }
    }

    @MockBean(ClientBcb::class)
    fun mockClientBcb(): ClientBcb {
        return Mockito.mock(ClientBcb::class.java)
    }

    @Factory
    class Client {
        @Singleton
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ): ShowKeyServiceGrpc.ShowKeyServiceBlockingStub {
            return ShowKeyServiceGrpc.newBlockingStub(channel)
        }
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

    private fun responseFindPixKey(): ResponseCreateAndFindPixKey {
        return ResponseCreateAndFindPixKey(
            keyType = EMAIL,
            key = "user.from.another.bank@santander.com.br",
            bankAccount = bankAccount(),
            owner = owner(),
            createdAt = LocalDateTime.now()
        )
    }

    private fun bankAccount(): BankAccount {
        return BankAccount(
            branch = "9871",
            accountNumber = "987654",
            accountType = BankAccountType.SVGS
        )
    }

    private fun owner(): Owner {
        return Owner(
            name = "Another User",
            taxIdNumber = "12345678901"
        )
    }
}