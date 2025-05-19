package com.example.easyfood.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.easyfood.databinding.FragmentProfileBinding
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.viewModel.SharedUserViewModel
import kotlinx.coroutines.launch
import androidx.fragment.app.activityViewModels

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val userViewModel: SharedUserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDao = MealDatabase.getInstance(requireContext()).userDao()

        userViewModel.loggedInUser.observe(viewLifecycleOwner) { user ->
            user?.let { currentUser ->
                binding.tvGreeting.text = "Hello, ${currentUser.firstName} ${currentUser.lastName}"

                binding.btnSaveChanges.setOnClickListener {
                    val newUsername = binding.etNewUsername.text.toString().trim()
                    val newPassword = binding.etNewPassword.text.toString().trim()

                    if (newUsername.isEmpty() && newPassword.isEmpty()) {
                        Toast.makeText(requireContext(), "Enter a new username or password", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val updatedUser = currentUser.copy(
                        username = if (newUsername.isNotEmpty()) newUsername else currentUser.username,
                        password = if (newPassword.isNotEmpty()) newPassword else currentUser.password
                    )

                    lifecycleScope.launch {
                        userDao.updateUser(updatedUser)
                        userViewModel.setUser(updatedUser)

                        Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_SHORT).show()
                        binding.etNewUsername.text.clear()
                        binding.etNewPassword.text.clear()
                    }
                }
            }
        }
    }
}
