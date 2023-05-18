package com.example.goshop.fragments.lognRegister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.goshop.R
import com.example.goshop.activities.ShoppingActivity
import com.example.goshop.data.User
import com.example.goshop.databinding.FragmentRegisterBinding
import com.example.goshop.util.Constants

import com.example.goshop.util.RegisterValidation
import com.example.goshop.util.Resource
import com.example.goshop.viewmodel.RegisterViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.PrivateKey

private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()
/*    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth*/

/*    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val user = User()
            handleResult(task, user)
        }
    }*/

/*    private fun handleResult(task: Task<GoogleSignInAccount>, user: User) {
        if (task.isSuccessful) {
            val account = task.result
            if (account != null) {
                updateUI(account,user)
            }
        }else {
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }*/

/*    private fun updateUI(account: GoogleSignInAccount, user: User) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){

                val user = User(
                    account.familyName.toString().trim(),
                    account.displayName.toString().trim(),
                    account.email.toString().trim()

                )
                viewModel.signInGoogle(user, account.photoUrl.toString())
                val intent = Intent(requireContext(), ShoppingActivity::class.java)
                startActivity(intent)

            }else{
                Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDoYouHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                val user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }



        /*auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestIdToken(Constants.default_web_client_id)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.googleRegister.setOnClickListener {
            val singInIntent = googleSignInClient.signInIntent
            launcher.launch(singInIntent)
        }*/


        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    is Resource.Error -> {
                        Log.e(TAG,it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}