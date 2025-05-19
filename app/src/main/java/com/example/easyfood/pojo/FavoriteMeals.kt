package com.example.easyfood.pojo

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "favoriteMeals",
    primaryKeys = ["userId", "mealId"],
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Meal::class, parentColumns = ["idMeal"], childColumns = ["mealId"], onDelete = ForeignKey.NO_ACTION)
    ]
)
data class FavoriteMeal(
    val userId: Int,
    val mealId: String
)
