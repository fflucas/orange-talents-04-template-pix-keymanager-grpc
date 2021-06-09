package br.com.zupacademy.key

import br.com.zupacademy.AccountType
import java.util.*
import javax.persistence.*
import javax.validation.Valid

@Entity
class Pix(

    @Column(nullable = false)
    val idOwner: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val keyType: KeyType,

    @Column(unique = true, nullable = false)
    var keyValue: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val accType: AccountType,

    @Embedded
    @field:Valid
    val associatedAcc: AssociatedAcc,
) {
    @Id
    val id: String = UUID.randomUUID().toString()

}