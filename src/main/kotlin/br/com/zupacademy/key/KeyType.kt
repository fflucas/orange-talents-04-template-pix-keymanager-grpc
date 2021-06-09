package br.com.zupacademy.key

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class KeyType {
    CPF{
        override fun isValidKey(key: String?): Boolean {
            if(key.isNullOrBlank()) return false

            if(!key.matches("^[0-9]{11}\$".toRegex())) return false

            return CPFValidator().run {
                initialize(null)
                isValid(key, null)
            }
        }
    },
    PHONE_NUMBER{
        override fun isValidKey(key: String?): Boolean {
            if(key.isNullOrBlank()) return false

            return key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL{
        override fun isValidKey(key: String?): Boolean {
            if(key.isNullOrBlank()) return false

            return EmailValidator().run {
                initialize(null)
                isValid(key, null)
            }
        }
    },
    RANDOM{
        override fun isValidKey(key: String?): Boolean {
            return key.isNullOrBlank()
        }
    };

    // abstract method to override with properly validations
    abstract fun isValidKey(key: String?): Boolean
}