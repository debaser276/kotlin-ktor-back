package ru.netology.dto

import ru.netology.model.AttachmentModel
import ru.netology.model.MediaType

data class MediaResponseDto (
    val id: String,
    val mediaType: MediaType
) {
    companion object {
        fun fromModel(model: AttachmentModel) = AttachmentModel(
            id = model.id,
            mediaType = model.mediaType
        )
    }
}