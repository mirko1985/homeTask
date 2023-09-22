package com.example.hometask.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CharacterData(
    val created: String,
    val episode: List<String>,
    val gender: String,
    @PrimaryKey val id: Int,
    val image: String,
    val location: Location,
    val name: String,
    val origin: Origin,
    val species: String,
    val status: String,
    val type: String,
    val url: String
)