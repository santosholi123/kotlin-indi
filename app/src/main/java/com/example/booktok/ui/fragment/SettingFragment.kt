package com.example.booktok.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.booktok.R
import com.example.booktok.ViewModel.UserViewModel
import com.example.booktok.databinding.FragmentSettingBinding
import com.example.booktok.repository.UserRepositoryImp
import com.example.booktok.ui.activity.LoginActivity

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        // Initialize ViewModel and Repository
        val repo = UserRepositoryImp()
        userViewModel = UserViewModel(repo)

        // Fetch current user and load data
        val currentUser = userViewModel.getCurrentUser()
        currentUser?.let {
            userViewModel.getUserFromDatabase(it.uid)
        }

        // Observe user data and update UI
        userViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.profileEmail.text = it.email
                binding.profileName.text = it.fName
                binding.editName.setText(it.fName)
                binding.editEmail.setText(it.email)
            }
        }

        // Initially hide both dropdowns and the switch
        binding.editProfileDropdown.visibility = View.GONE
        binding.settingsDropdown.visibility = View.GONE


        // Toggle edit profile dropdown
        binding.editProfile.setOnClickListener {
            if (binding.editProfileDropdown.visibility == View.GONE) {
                binding.editProfileDropdown.visibility = View.VISIBLE
                binding.settingsDropdown.visibility = View.GONE

            } else {
                binding.editProfileDropdown.visibility = View.GONE
            }
        }

        // Toggle settings dropdown and show/hide switch


        // Save updated profile
        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.editName.text.toString()
            val newEmail = binding.editEmail.text.toString()
            if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                currentUser?.uid?.let { uid ->
                    val updatedUser = userViewModel.userData.value?.copy(
                        fName = newName,
                        email = newEmail
                    )
                    updatedUser?.let {
                        userViewModel.addUserToDatabase(uid, it) { success, message ->
                            if (success) {
                                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT)
                                    .show()
                                binding.editProfileDropdown.visibility = View.GONE
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle logout
        binding.logout.setOnClickListener {
            userViewModel.logout { success, message ->
                if (success) {
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}