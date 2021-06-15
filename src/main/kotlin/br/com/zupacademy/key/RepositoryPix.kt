package br.com.zupacademy.key

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface RepositoryPix: JpaRepository<Pix, UUID> {
    fun existsByKeyValue(keyValue: String): Boolean
    fun findByIdAndIdOwner(id: UUID, idOwner: String): Optional<Pix>
    fun findByKeyValue(pixKey: String): Optional<Pix>
    fun findAllByIdOwner(idOwner: String): List<Pix>
}