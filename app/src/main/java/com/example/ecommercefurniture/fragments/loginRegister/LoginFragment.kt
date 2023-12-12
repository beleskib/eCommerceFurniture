package com.example.ecommercefurniture.fragments.loginRegister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommercefurniture.Activities.ShoppingActivity
import com.example.ecommercefurniture.R
import com.example.ecommercefurniture.databinding.FragmentLoginBinding
import com.example.ecommercefurniture.dialog.setUpBottomSheetDialog
import com.example.ecommercefurniture.util.Resource
import com.example.ecommercefurniture.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
    
    val TAG: String = "LoginFragment"
    val GOOGLE_REQ_CODE = 13
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

   /* override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Settings.Global.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.IVGoogleLogin.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        firestore.collection("user").document(auth.uid!!).get()
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account : GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        }else {
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful) {
                val intent : Intent = Intent(requireContext(), ShoppingActivity::class.java)
                startActivity(intent)
            }else {
                Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onFacebookSignIn()

        
        binding.IVfacebookLogin.setOnClickListener {
            Toast.makeText(requireContext(), "Cannot login with Facebook right now, try a different way", Toast.LENGTH_SHORT).show()
        }

        binding.tvDontHaveanAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.apply {
            btnLoginLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email, password)
            }
        }

        binding.tvForgotPasswordLogin.setOnClickListener {
            setUpBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.resetpw.collect{
                when(it) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                            Snackbar.make(requireView(), "Reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                        }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when(it) {
                    is Resource.Loading -> {
                        binding.btnLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnLoginLogin.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also {
                            intent -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.btnLoginLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_REQ_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("test,",account.email.toString())
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                viewModel.signInWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e)
                Toast.makeText(requireContext(), "There was a problem connecting with Google, please register by clicking (Register now!.) ", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun onFacebookSignIn() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.IVGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_REQ_CODE)
        }
    }
}