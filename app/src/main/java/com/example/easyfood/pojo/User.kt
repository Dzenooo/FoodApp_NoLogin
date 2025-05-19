package com.example.easyfood.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userInformation")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String
)
