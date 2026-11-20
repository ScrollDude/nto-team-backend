package ru.nto.team

import org.springframework.stereotype.Service

@Service
class StaffService(
    private val staffRepository: StaffRepository
) {
    fun getAuthenticatedStaff(code: String): Staff {
        if (!Regex("[A-Za-z0-9]{4}").matches(code)) {
            throw ApiException(400, "Invalid code format")
        }

        return staffRepository.findByCode(code)
            ?: throw ApiException(401, "Unauthorized")
    }

    fun profile(code: String): ProfileDto {
        val staff = getAuthenticatedStaff(code)
        return ProfileDto(name = staff.fullName)
    }
}
