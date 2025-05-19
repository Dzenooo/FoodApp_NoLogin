package com.example.easyfood.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.pojo.Category
import com.example.easyfood.pojo.CategoryList
import com.example.easyfood.pojo.FavoriteMeal
import com.example.easyfood.pojo.MealsByCategoryList
import com.example.easyfood.pojo.MealsByCategory
import com.example.easyfood.pojo.Meal
import com.example.easyfood.pojo.MealList
import com.example.easyfood.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    val mealDatabase: MealDatabase
): ViewModel() {

    private var randomMealLiveData = MutableLiveData<Meal>()
    private var popularItemsLiveData = MutableLiveData<List<MealsByCategory>>()
    private var categoriesLiveData = MutableLiveData<List<Category>>()
    private var favoriteMealsLiveData = mealDatabase.mealDao().getAllMeals()
    private var bottomSheetMealLiveData = MutableLiveData<Meal>()
    private var searchedMealsLiveData = MutableLiveData<List<Meal>>()
    private val favoriteMealsLiveDataPerUser = MutableLiveData<List<Meal>>()

    fun getRandomMeal(){
        RetrofitInstance.api.getRandomMeal().enqueue(object : Callback<MealList> {

            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if(response.body() != null){
                    val randomMeal: Meal = response.body()!!.meals[0]
                    randomMealLiveData.value = randomMeal
                }
                else{
                    return
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("HomeFragment", t.message.toString())
            }
        })
    }

    fun getPopularItems(){

        RetrofitInstance.api.getPopularItems("Seafood").enqueue(object : Callback<MealsByCategoryList>{
            override fun onResponse(call: Call<MealsByCategoryList>, response: Response<MealsByCategoryList>) {
                if(response.body()!=null){
                    popularItemsLiveData.value = response.body()!!.meals
                }
            }

            override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                Log.d("HomeFragment", t.message.toString())
            }

        })
    }

    fun getCategories(){

        RetrofitInstance.api.getCategories().enqueue(object : Callback<CategoryList>{
            override fun onResponse(call: Call<CategoryList?>,response: Response<CategoryList?>) {

                response.body()?.let {categoryList ->
                    categoriesLiveData.postValue(categoryList.categories)
                }
            }

            override fun onFailure(call: Call<CategoryList?>,t: Throwable) {

                Log.e("HomeViewModel", t.message.toString())
            }

        })
    }

    fun getMealById(id: String){

        RetrofitInstance.api.getMealDetails(id).enqueue(object : Callback<MealList>{
            override fun onResponse(
                call: Call<MealList?>,
                response: Response<MealList?>
            ) {
                val meal = response.body()?.meals?.first()
                meal?.let {meal->
                    bottomSheetMealLiveData.postValue(meal)
                }
            }

            override fun onFailure(
                call: Call<MealList?>,
                t: Throwable
            ) {
                Log.e("HomeViewModel", t.message.toString())
            }

        })
    }



    fun searchMeals(searchQuery:String) = RetrofitInstance.api.searchMeals(searchQuery).enqueue(
        object : Callback<MealList>{
            override fun onResponse(
                call: Call<MealList?>,
                response: Response<MealList?>
            ) {
                val mealList = response.body()?.meals

                mealList?.let {
                    searchedMealsLiveData.postValue(it)
                }
            }

            override fun onFailure(
                call: Call<MealList?>,
                t: Throwable
            ) {
                Log.e("HomeViewModel", t.message.toString())
            }

        }
    )

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

    fun deleteFavoriteForUser(userId: Int, mealId: String) = viewModelScope.launch {
        Log.d("HomeViewModel", "🔥 deleteFavoriteForUser called for user=$userId, meal=$mealId")
        mealDatabase.favoriteMealsDao().deleteFavorite(userId, mealId)

        loadFavoriteMealsForUser(userId)
    }



    // Replace your MutableLiveData + manual load entirely:
    fun observeFavoritesForUser(userId: Int): LiveData<List<Meal>> =
        mealDatabase.favoriteMealsDao().getFavoriteMealsForUserLive(userId)


    fun observeSearchedMealsLiveData() : LiveData<List<Meal>> = searchedMealsLiveData

    fun observeRandomMealLiveData():LiveData<Meal>{
        return randomMealLiveData
    }

    fun observePopularItemsLiveData():LiveData<List<MealsByCategory>>{
      return popularItemsLiveData
    }

    fun observeCategoriesLiveData(): LiveData<List<Category>>{
        return categoriesLiveData
    }



    fun observeBottomSheetMeal() : LiveData<Meal> = bottomSheetMealLiveData
}