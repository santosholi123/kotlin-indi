package com.example.booktok.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booktok.model.UserModel
import com.example.booktok.R
import com.example.booktok.ViewModel.UserViewModel
import com.example.booktok.databinding.ActivityRegistrationBinding
import com.example.booktok.repository.UserRepositoryImp
import com.example.booktok.utils.LoadingUtils
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrationBinding

    lateinit var userViewModel: UserViewModel

    lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val userRepository = UserRepositoryImp()

        userViewModel = UserViewModel(userRepository)

        loadingUtils = LoadingUtils(this)

        binding.btnSignIn.setOnClickListener {
            loadingUtils.show()
            var email: String = binding.email.text.toString()
            var password: String = binding.Password.text.toString()
            var fName: String = binding.userName.text.toString()
            var conformpassword: String = binding.ConformPassword.text.toString()
            var contact: String = binding.phoneNumber.text.toString()

            userViewModel.signup(email,password){
                    success,message,userId ->
                if(success){
                    val userModel = UserModel(
                        userId,
                        email, fName,  contact
                    )
                    addUser(userModel)
                }else{
                    loadingUtils.dismiss()
                    Toast.makeText(this@RegisterActivity,
                        message,Toast.LENGTH_SHORT).show()
                }


            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    fun addUser(userModel: UserModel) {
        // Log before starting the operation
        Log.d("RegisterActivity", "Starting to add user: ${userModel.userId}")

        userViewModel.addUserToDatabase(userModel.userId, userModel) { success, message ->
            // Log the result of the operation
            if (success) {
                Log.d("RegisterActivity", "User added successfully: $message")
                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // For errors, use Log.e (error)
                Log.e("RegisterActivity", "Failed to add user: $message")
                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
            }

            // Log when the loading is dismissed
            Log.d("RegisterActivity", "Loading dismissed")
            loadingUtils.dismiss()
        }
    }
}