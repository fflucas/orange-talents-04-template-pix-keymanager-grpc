package br.com.zupacademy.key.show

import br.com.zupacademy.RequestShowKey
import br.com.zupacademy.RequestShowKey.FilterCase.*
import javax.validation.ConstraintViolationException
import javax.validation.Validation

fun RequestShowKey.toModel(
): Filter {
    // lógica de qual método de busca será usado
    val filter = when(filterCase!!){
        PIX_ID -> pixId.let {
            Filter.ByPixId(idOwner = it.idOwner, idPix = it.idPix)
        }
        PIX_KEY -> Filter.ByPixKey(pixKey = pixKey)
        FILTER_NOT_SET -> Filter.Invalid()
    }

    // constroi um validator
    val validator = Validation.buildDefaultValidatorFactory().validator
    // aplica as validações nos parâmetros dos construtores
    val violations = validator.validate(filter)
    if(violations.isNotEmpty()){
        throw ConstraintViolationException(violations)
    }

    return filter
}