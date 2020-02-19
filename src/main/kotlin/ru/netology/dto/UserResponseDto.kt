package ru.netology.dto

import ru.netology.model.UserModel

data class UserResponseDto(val id: Int, val username: String) {
    companion object {
        fun fromModel(model: UserModel) = UserResponseDto(
            id = model.id,
            username = model.username
        )
    }
}