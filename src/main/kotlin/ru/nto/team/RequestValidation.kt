package ru.nto.team

import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

object RequestValidation {
    private val formatter = DateTimeFormatter
        .ofPattern("uuuu-MM-dd'T'HH:mm:ss")
        .withResolverStyle(ResolverStyle.STRICT)

    fun normalize(value: String?): String? = value?.trim()

    private fun validateFullName(fullName: String?): String? {
        if (fullName.isNullOrEmpty()) {
            return "ФИО не может быть пустым"
        }

        if (fullName.length !in 5..150) {
            return "ФИО должно содержать от 5 до 150 символов"
        }

        return null
    }

    private fun validatePhone(phone: String?): String? {
        if (phone.isNullOrEmpty()) {
            return "Номер телефона не может быть пустым"
        }

        if (!Regex("\\+7[0-9]{10}").matches(phone)) {
            return "Номер должен иметь формат +7 и десять цифр"
        }

        return null
    }

    fun parseDate(value: String): LocalDateTime? {
        if (!Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}").matches(value)) {
            return null
        }

        return try {
            LocalDateTime.parse(value, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    private fun validateValidFrom(value: LocalDateTime, clock: Clock): String? {
        val now = LocalDateTime.now(clock)

        if (value.isBefore(now)) {
            return "Дата не может быть в прошлом"
        }

        return null
    }

    private fun validateValidTo(
        validFrom: LocalDateTime,
        validTo: LocalDateTime
    ): String? {

        if (!validTo.isAfter(validFrom)) {
            return "Дата окончания должна быть позже начала"
        }

        if (validFrom.toLocalDate() != validTo.toLocalDate()) {
            return "Даты должны быть в один день"
        }

        return null
    }

    private fun validateOrganization(value: String?): String? {
        if (value == null) return null

        if (value.length > 150) {
            return "Название организации слишком длинное"
        }

        return null
    }

    private fun validatePurpose(value: String?): String? {
        if (value.isNullOrEmpty()) {
            return "Цель посещения обязательна"
        }

        if (value.length !in 5..200) {
            return "Цель посещения должна содержать от 5 до 200 символов"
        }

        return null
    }

    fun validate(
        fullName: String?,
        phone: String?,
        organization: String?,
        purpose: String?,
        validFrom: String?,
        validTo: String?,
        clock: Clock
    ): Map<String, String> {
        val errors = linkedMapOf<String, String>()

        validateFullName(fullName)?.let {
            errors["fullName"] = it
        }

        validatePhone(phone)?.let {
            errors["phone"] = it
        }

        validateOrganization(organization)?.let {
            errors["organization"] = it
        }

        validatePurpose(purpose)?.let {
            errors["purpose"] = it
        }

        val from = validFrom?.let { parseDate(it) }

        if (from == null) {
            errors["validFrom"] = "Некорректный формат даты"
        } else {
            validateValidFrom(from, clock)?.let {
                errors["validFrom"] = it
            }
        }

        val to = validTo?.let { parseDate(it) }

        if (to == null) {
            errors["validTo"] = "Некорректный формат даты"
        } else if (from != null) {
            validateValidTo(from, to)?.let {
                errors["validTo"] = it
            }
        }

        return errors
    }

    fun validateDecision(body: DecisionBody): Map<String, String> {
        val errors = linkedMapOf<String, String>()

        val status = body.status?.trim()
        val comment = body.comment?.trim()

        if (status !in listOf("APPROVED", "REJECTED")) {
            errors["status"] = "Недопустимый статус"
        }

        if (status == "REJECTED" &&
            (comment == null || comment.length !in 1..500)
        ) {
            errors["comment"] = "Комментарий должен содержать 1–500 символов"
        }

        return errors
    }
}

class RequestValidationException(
    val errors: Map<String, String>
) : RuntimeException("Request validation failed")
