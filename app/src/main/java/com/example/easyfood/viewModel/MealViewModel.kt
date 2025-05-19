package com.example.easyfood.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.pojo.FavoriteMeal
import com.example.easyfood.pojo.Meal
import com.example.easyfood.pojo.MealList
import com.example.easyfood.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealViewModel(
    val mealDatabase: MealDatabase
):ViewModel() {

    private var mealDetailLiveData = MutableLiveData<Meal>()
    private val favoriteMealsLiveDataPerUser = MutableLiveData<List<Meal>>()


    fun getMealDetail(id:String){
        RetrofitInstance.api.getMealDetails(id).enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if(response.body() != null){
                    mealDetailLiveData.value = response.body()!!.meals[0]
                }
                else return
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("MealActivity", t.message.toString())
            }


        })
    }
    fun loadFavoriteMealsForUser(userId: Int) {
        viewModelScope.launch {
            val favorites = mealDatabase.favoriteMealsDao().getFavoriteMealsForUser(userId)
            Log.d("Favorites", "Loaded ${favorites.size} favorites for user $userId")
            favoriteMealsLiveDataPerUser.postValue(favorites)
        }
    }

    fun addFavoriteForUser(userId: Int, meal: Meal) = viewModelScope.launch {
        mealDatabase.mealDao().upsertMeal(meal)
        mealDatabase.favoriteMealsDao().addFavorite(FavoriteMeal(userId, meal.idMeal))
        loadFavoriteMealsForUser(userId)
    }





    fun observeFavoriteMealsLiveDataPerUser(): LiveData<List<Meal>> = favoriteMealsLiveDataPerUser
    fun observerMealDetailsLiveData():LiveData<Meal>{
        return mealDetailLiveData
    }

   fun insertMeal(meal: Meal){
        viewModelScope.launch {
            mealDatabase.mealDao().upsertMeal(meal)
        }
    }


}