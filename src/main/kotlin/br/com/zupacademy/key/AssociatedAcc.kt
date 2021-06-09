package br.com.zupacademy.key

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
class AssociatedAcc(

    @field:NotBlank
    @Column(nullable = false)
    val bank: String,

    @field:NotBlank
    @Column(nullable = false)
    val owner: String,

    @field:NotBlank
    @field:Size(max = 11)
    @Column(nullable = false)
    val cpf: String,

    @field:NotBlank
    @Column(nullable = false)
    val agency: String,

    @field:NotBlank
    @Column(nullable = false)
    val account: String,
)