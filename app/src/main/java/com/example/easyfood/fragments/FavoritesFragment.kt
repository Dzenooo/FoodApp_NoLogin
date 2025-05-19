package com.example.easyfood.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.easyfood.activities.MainActivity
import com.example.easyfood.activities.MealActivity
import com.example.easyfood.adapters.MealsAdapter
import com.example.easyfood.databinding.FragmentFavoritesBinding
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.fragments.HomeFragment.Companion.MEAL_ID
import com.example.easyfood.fragments.HomeFragment.Companion.MEAL_NAME
import com.example.easyfood.fragments.HomeFragment.Companion.MEAL_THUMB
import com.example.easyfood.viewModel.HomeViewModel
import com.example.easyfood.viewModel.MealViewModel
import com.example.easyfood.viewModel.MealViewModelFactory
import com.example.easyfood.viewModel.SharedUserViewModel
import com.google.android.material.snackbar.Snackbar


class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: HomeViewModel


    private lateinit var favoritesAdapter: MealsAdapter

    private val userViewModel: SharedUserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()



        onFavoriteItemClick()


        userViewModel.loggedInUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // **Directly observe** the LiveData from Room
                viewModel.observeFavoritesForUser(user.id)
                    .observe(viewLifecycleOwner) { list ->
                        favoritesAdapter.differ.submitList(list)
                    }
            } else {
                favoritesAdapter.differ.submitList(emptyList())
            }
        }


        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position = viewHolder.adapterPosition
                val deletedMeal = favoritesAdapter.differ.currentList[position]
                val currentUser = userViewModel.loggedInUser.value ?: return



                    // Delete only the favorite relation between current user and meal
                    viewModel.deleteFavoriteForUser(currentUser.id, deletedMeal.idMeal)

                    Snackbar.make(requireView(), "Meal removed from favorites", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            viewModel.addFavoriteForUser(currentUser.id, deletedMeal)
                        }
                        .show()

            }


        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvFavorites)

    }

    private fun onFavoriteItemClick() {

            favoritesAdapter.onItemClick = {meal ->
                val intent = Intent(activity, MealActivity::class.java)
                intent.putExtra(MEAL_ID, meal.idMeal)
                intent.putExtra(MEAL_NAME, meal.strMeal)
                intent.putExtra(MEAL_THUMB, meal.strMealThumb)
                startActivity(intent)
            }
        }


    private fun prepareRecyclerView() {

        favoritesAdapter = MealsAdapter()
        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = favoritesAdapter
        }

    }

    /*private fun observeFavorites() {
        viewModel.observeFavoritesForUser().observe(viewLifecycleOwner, {meals->
            favoritesAdapter.differ.submitList(meals)
        })
    }*/
}
