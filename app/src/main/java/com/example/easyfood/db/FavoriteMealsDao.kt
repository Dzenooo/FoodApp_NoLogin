package com.example.easyfood.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.easyfood.pojo.FavoriteMeal
import com.example.easyfood.pojo.Meal

@Dao
interface FavoriteMealDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favoriteMeal: FavoriteMeal)

    @Delete
    suspend fun removeFavorite(favoriteMeal: FavoriteMeal)

    @Query("""
  SELECT m.* 
  FROM mealInformation m
    INNER JOIN favoriteMeals f ON m.idMeal = f.mealId
  WHERE f.userId = :userId
""")
    suspend fun getFavoriteMealsForUser(userId: Int): List<Meal>

    @Query("""
  SELECT m.* 
  FROM mealInformation m
    INNER JOIN favoriteMeals f ON m.idMeal = f.mealId
  WHERE f.userId = :userId
""")
    fun getFavoriteMealsForUserLive(userId: Int): LiveData<List<Meal>>

// (Keep the old suspend version if you need it elsewhere—but we’ll use the LiveData one here.)




    @Query("DELETE FROM favoriteMeals WHERE userId = :userId AND mealId = :mealId")
    suspend fun deleteFavorite(userId: Int, mealId: String)


}


