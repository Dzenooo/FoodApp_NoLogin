package com.example.easyfood.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.easyfood.R
import com.example.easyfood.UserSessionManager
import com.example.easyfood.databinding.ActivityMealBinding
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.fragments.HomeFragment
import com.example.easyfood.pojo.Meal
import com.example.easyfood.viewModel.HomeViewModel
import com.example.easyfood.viewModel.MealViewModel
import com.example.easyfood.viewModel.MealViewModelFactory
import com.example.easyfood.viewModel.SharedUserViewModel

class MealActivity : AppCompatActivity() {

    private lateinit var mealId:String
    private lateinit var mealName:String
    private lateinit var mealThumb:String

    private lateinit var binding:ActivityMealBinding

    private lateinit var mealMvvm:MealViewModel

    private lateinit var youtubeLink:String

    private val userViewModel: SharedUserViewModel by lazy {
        ViewModelProvider(this).get(SharedUserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mealDatabase = MealDatabase.getInstance(this)
        val viewModelFactory = MealViewModelFactory(mealDatabase)
        mealMvvm = ViewModelProvider(this, viewModelFactory).get(MealViewModel::class.java)

        getMealInformationFromIntent()

        setInformationInViews()

        loadingCase()

        mealMvvm.getMealDetail(mealId)
        observerMealDetailsLiveData()

        onYoutubeImageClick()
        onFavoriteClick()
    }

    private fun onFavoriteClick() {
        binding.buttonFavorites.setOnClickListener {
            val userIdFromViewModel = userViewModel.loggedInUser.value?.id
            val userIdFromSession = UserSessionManager.getLoggedInUserId(this).takeIf { it != -1 }

            val userId = userIdFromViewModel ?: userIdFromSession

            if (userId == null) {
                Toast.makeText(this, "Please log in to save favorites", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mealToSave?.let { meal ->
                mealMvvm.addFavoriteForUser(userId, meal)
                Toast.makeText(this, "Meal saved to your favorites", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun onYoutubeImageClick() {
        binding.imgYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
            startActivity(intent)
        }
    }

   private var mealToSave:Meal?=null
    private fun observerMealDetailsLiveData() {
        mealMvvm.observerMealDetailsLiveData().observe(this, object : Observer<Meal>{
            override fun onChanged(t: Meal) {
                onResponseCase()
                val meal = t
                mealToSave = meal

                binding.tvCategories.text = "Category : ${meal!!.strCategory}"
                binding.tvArea.text = "Area : ${meal.strArea}"
                binding.tvInstructionsDetails.text = meal.strInstructions

                youtubeLink = meal.strYoutube.toString()
            }
        })
    }

    private fun setInformationInViews() {
        Glide.with(applicationContext)
            .load(mealThumb)
            .into(binding.imgMealDetail)


        binding.collapsingToolbar.title = mealName
        binding.collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
        binding.collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))


    }

    private fun getMealInformationFromIntent() {
        val intent = intent

        mealId = intent.getStringExtra(HomeFragment.MEAL_ID)!!
        mealName = intent.getStringExtra(HomeFragment.MEAL_NAME)!!
        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB)!!
    }

    private fun loadingCase(){

        binding.progressBar.visibility = View.VISIBLE

        binding.buttonFavorites.visibility = View.INVISIBLE
        binding.tvArea.visibility = View.INVISIBLE
        binding.tvCategories.visibility = View.INVISIBLE
        binding.tvInstructions.visibility = View.INVISIBLE
        binding.imgYoutube.visibility = View.INVISIBLE


    }
    private fun onResponseCase(){

        binding.progressBar.visibility = View.INVISIBLE

        binding.buttonFavorites.visibility = View.VISIBLE
        binding.tvArea.visibility = View.VISIBLE
        binding.tvCategories.visibility = View.VISIBLE
        binding.tvInstructions.visibility = View.VISIBLE
        binding.imgYoutube.visibility = View.VISIBLE


    }
}