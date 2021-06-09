package br.com.zupacademy.key

import br.com.zupacademy.AccountType
import java.util.*
import javax.persistence.*

@Entity
class Pix(
    @Column(nullable = false)
    val owner: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val keyType: PixKeyType,

    @Column(unique = true, nullable = false)
    var keyValue: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val accType: AccountType
) {
    @Id
    val id: String = UUID.randomUUID().toString()

    init {
        if (keyValue.isBlank()){
            keyValue = UUID.randomUUID().toString()
        }
    }
}