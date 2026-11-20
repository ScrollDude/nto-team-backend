package ru.nto.team

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

class ApiException(val status: Int, message: String) : RuntimeException(message)

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(RequestValidationException::class)
    fun handleValidation(
        error: RequestValidationException
    ): ResponseEntity<Map<String, Any>> {
        return ResponseEntity
            .badRequest()
            .body(mapOf("errors" to error.errors))
    }

    @ExceptionHandler(ApiException::class)
    fun handleApiException(
        error: ApiException
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(error.status)
            .body(mapOf("error" to error.message.orEmpty()))
    }
}
