package ru.netology.dto

import ru.netology.model.MediaModel
import ru.netology.model.MediaType

data class MediaResponseDto (
    val id: String,
    val mediaType: MediaType
) {
    companion object {
        fun fromModel(media: MediaModel) = MediaModel(
            id = media.id,
            mediaType = media.mediaType
        )
    }
}