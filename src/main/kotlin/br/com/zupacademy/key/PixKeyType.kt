package br.com.zupacademy.key

enum class PixKeyType {
    CPF{
        override fun isValidKey(key: String): Boolean {
            return key.matches("^[0-9]{11}\$".toRegex())
        }
    },
    PHONE_NUMBER{
        override fun isValidKey(key: String): Boolean {
            return key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL{
        override fun isValidKey(key: String): Boolean {
             return key.matches(
                 "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])"
                     .toRegex()
             )
        }
    },
    RANDOM{
        override fun isValidKey(key: String): Boolean {
            return key.isBlank()
        }
    };

    // abstract method to override with properly validations
    abstract fun isValidKey(key: String): Boolean
}