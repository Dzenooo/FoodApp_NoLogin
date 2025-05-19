package com.example.easyfood.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.easyfood.pojo.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User)

    @Update
    suspend fun updateUser(user: User)


    @Query("SELECT * FROM userInformation WHERE username = :u AND password = :p LIMIT 1")
    suspend fun login(u: String, p: String): User?

    @Query("SELECT COUNT(*) FROM userInformation WHERE username = :u")
    suspend fun exists(u: String): Int

    @Query("SELECT * FROM userInformation WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM userInformation WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

}
