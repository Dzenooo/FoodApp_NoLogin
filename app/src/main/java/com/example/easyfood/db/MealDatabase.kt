package com.example.easyfood.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.easyfood.pojo.FavoriteMeal
import com.example.easyfood.pojo.Meal
import com.example.easyfood.pojo.User

@Database(entities = [Meal::class, User::class, FavoriteMeal::class], version = 3)
@TypeConverters(MealTypeConverter::class)



abstract class MealDatabase : RoomDatabase(){


    abstract fun mealDao() : MealDao
    abstract fun userDao() : UserDao
    abstract fun favoriteMealsDao() : FavoriteMealDao


    companion object{
        @Volatile
        var INSTANCE: MealDatabase? = null

        @Synchronized
        fun getInstance(context: Context): MealDatabase{
            if(INSTANCE==null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    MealDatabase::class.java,
                    "meal.db"
                ).fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE as MealDatabase
        }
    }

}