package br.com.zupacademy.key

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface RepositoryPix: JpaRepository<Pix, String> {
    fun findByKeyValue(keyValue: String): Optional<Pix>
}