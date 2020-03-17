package ru.netology.model

enum class MediaType {
    IMAGE, NOMEDIA
}

data class AttachmentModel(
    val id: String?,
    val mediaType: MediaType = MediaType.NOMEDIA
)