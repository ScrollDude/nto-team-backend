package ru.nto.team

import java.time.LocalDateTime

data class ProfileDto(
    val name: String
)

data class RequestListDto(
    val id: Long,
    val fullName: String,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val status: RequestStatus
)
data class RequestDetailsDto(
    val id: Long,
    val fullName: String,
    val phone: String,
    val organization: String?,
    val purpose: String,
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val status: RequestStatus,
    val comment: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
