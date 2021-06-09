package br.com.zupacademy.util.errors

import io.micronaut.aop.Around
import io.micronaut.context.annotation.Type
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Type(ExceptionHandlerInterceptor::class)
@Around
annotation class ErrorHandler