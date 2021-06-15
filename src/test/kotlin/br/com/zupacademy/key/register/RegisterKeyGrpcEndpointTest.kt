package br.com.zupacademy.key.register

import br.com.zupacademy.AccountType
import br.com.zupacademy.KeyType
import br.com.zupacademy.RegisterKeyServiceGrpc
import br.com.zupacademy.RequestNewKey
import br.com.zupacademy.bcb.*
import br.com.zupacademy.erp.itau.ClientErpItau
import br.com.zupacademy.erp.itau.ResponseConsultaConta
import br.com.zupacademy.erp.itau.ResponseInstituicao
import br.com.zupacademy.erp.itau.ResponseTitular
import br.com.zupacademy.key.KeyType.EMAIL
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// transação é desativada pq o gRPC Server roda em thread separada
@MicronautTest(transactional = false)
internal class RegisterKeyGrpcEndpointTest(
    private val repositoryPix: RepositoryPix,
    private val grpcClient: RegisterKeyServiceGrpc.RegisterKeyServiceBlockingStub
){

    @Inject
    lateinit var clientBcb: ClientBcb
    @Inject
    lateinit var clientErpItau: ClientErpItau

    companion object{
        val CLIENT_ID: UUID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup(){
        repositoryPix.deleteAll()
    }

    @Test
    fun `it should be able to register a new pix key`(){
        // cenario
        val keyType = KeyType.EMAIL
        val keyValue = "fabio@teste.com"

        `when`(clientErpItau.consulta_contas(id = CLIENT_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(responseConsultaConta())

        var testRequestCreatePixKey = requestCreatePixKey(
            keyType.name,
            keyValue
        )
        `when`(clientBcb.createPixKey(testRequestCreatePixKey))
            .thenReturn(HttpResponse.created(responseCreatePixKey(testRequestCreatePixKey)))

        // acao
        val response = grpcClient.registerKey(
            RequestNewKey.newBuilder()
                .setIdOwner(CLIENT_ID.toString())
                .setKeyType(keyType)
                .setKeyValue(keyValue)
                .setAccType(AccountType.CONTA_CORRENTE)
                .build()
        )

        // resultado
        with(response){
            assertNotNull(pixId)
        }

    }

    @Test
    fun `it should not be possible to register a new pix key if it already exists`(){
        // cenario
        repositoryPix.save(newPixKey(
            keyType = EMAIL,
            keyValue = "fabio@teste.com",
            ownerId = CLIENT_ID.toString(),
        ))

        // acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registerKey(
                RequestNewKey.newBuilder()
                    .setIdOwner(CLIENT_ID.toString())
                    .setKeyType(KeyType.EMAIL)
                    .setKeyValue("fabio@teste.com")
                    .setAccType(AccountType.CONTA_CORRENTE)
                    .build()
            )
        }

        // resultado
        with(thrown){
            assertEquals(Status.ALREADY_EXISTS.code ,status.code)
            assertEquals("Chave pix fabio@teste.com já foi cadastrada", status.description)
        }
    }

    @Test
    fun `it should not be possible to register a new pix key if the user was not returned by the itau client`(){
        // cenario
        `when`(clientErpItau.consulta_contas(CLIENT_ID.toString(), "CONTA_CORRENTE"))
            .thenReturn(null)

        // acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registerKey(
                RequestNewKey.newBuilder()
                    .setIdOwner(CLIENT_ID.toString())
                    .setKeyType(KeyType.EMAIL)
                    .setKeyValue("fabio@teste.com")
                    .setAccType(AccountType.CONTA_CORRENTE)
                    .build()
            )
        }

        // resultado
        with(thrown){
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Cliente $CLIENT_ID não encontrado no ERP Itau", status.description)
        }
    }

    @Test
    fun `it should not be possible to register a new pix key if the BCB Client did not respond successfully`(){
        // cenario
        val keyType = KeyType.EMAIL
        val keyValue = "fabio@teste.com"

        `when`(clientErpItau.consulta_contas(CLIENT_ID.toString(), "CONTA_CORRENTE"))
            .thenReturn(responseConsultaConta())

        `when`(clientBcb.createPixKey(requestCreatePixKey(keyType.name, keyValue)))
            .thenReturn(HttpResponse.badRequest())

        // acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registerKey(
                RequestNewKey.newBuilder()
                    .setIdOwner(CLIENT_ID.toString())
                    .setKeyType(keyType)
                    .setKeyValue(keyValue)
                    .setAccType(AccountType.CONTA_CORRENTE)
                    .build()
            )
        }

        // resultado
        with(thrown){
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("BCB não conseguiu cadastrar a chave pix", status.description)
        }
    }

    @Test
    fun `it should not be possible to register a new pix key if the request parameters are missing`(){
        // cenario
        val grpcRequest = RequestNewKey.newBuilder().build()

        // acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registerKey(grpcRequest)
        }

        // resultado
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Factory
    class Client{
        @Singleton
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ): RegisterKeyServiceGrpc.RegisterKeyServiceBlockingStub {
            return RegisterKeyServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ClientErpItau::class)
    fun mockErpItauClient(): ClientErpItau? {
        return Mockito.mock(ClientErpItau::class.java)
    }

    @MockBean(ClientBcb::class)
    fun mockBcbClient(): ClientBcb {
        return Mockito.mock(ClientBcb::class.java)
    }

    private fun responseConsultaConta(): ResponseConsultaConta {
        return ResponseConsultaConta(
            tipo = AccountType.CONTA_CORRENTE,
            instituicao = ResponseInstituicao(
                nome = "ITAÚ UNIBANCO S.A.",
                ispb = "60701190"
            ),
            agencia = "0001",
            numero = "291900",
            titular = ResponseTitular(
                id = CLIENT_ID.toString(),
                nome = "Rafael M C Ponte",
                cpf = "02467781054"
            ),
        )
    }

    private fun newPixKey(
        keyType: br.com.zupacademy.key.KeyType,
        keyValue: String,
        ownerId: String,
    ): Pix {
        return Pix(
            idOwner = ownerId,
            keyType = keyType,
            keyValue = keyValue,
            accType = AccountType.CONTA_CORRENTE,
            associatedAcc = responseConsultaConta().convertToAssociateAccount(),
            createdAt = LocalDateTime.now()
        )
    }

    private fun requestCreatePixKey(
        keyType: String,
        keyValue: String?
    )
    : RequestCreatePixKey {
        val responseClientAcc = responseConsultaConta()

        return RequestCreatePixKey(
            keyType = keyType,
            key = keyValue,
            bankAccount = BankAccount(
                branch = responseClientAcc.agencia,
                accountNumber = responseClientAcc.numero,
                accountType = BankAccountType.convertFromAccountType(responseClientAcc.tipo)
            ),
            owner = Owner(
                name = responseClientAcc.titular.nome,
                taxIdNumber = responseClientAcc.titular.cpf
            )
        )
    }

    private fun responseCreatePixKey(
        request: RequestCreatePixKey
    ): ResponseCreateAndFindPixKey {
        return ResponseCreateAndFindPixKey(
            keyType = br.com.zupacademy.key.KeyType.valueOf(request.keyType),
            key = if (request.keyType == br.com.zupacademy.key.KeyType.RANDOM.name) UUID.randomUUID().toString() else request.key!!,
            bankAccount = request.bankAccount,
            owner = request.owner,
            createdAt = LocalDateTime.now(),
        )
    }
}