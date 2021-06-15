package br.com.zupacademy.key

import br.com.zupacademy.AccountType
import java.time.LocalDateTime
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

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    fun belongsTo(idOwner: String): Boolean {
        return this.idOwner == idOwner
    }

    @Id
    @GeneratedValue
    @Column(length = 16)
    val id: UUID? = null

}