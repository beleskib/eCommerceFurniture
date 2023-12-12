package com.example.ecommercefurniture.fragments.loginRegister


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommercefurniture.R
import com.example.ecommercefurniture.data.User
import com.example.ecommercefurniture.databinding.FragmentRegisterBinding
import com.example.ecommercefurniture.util.RegisterValidation
import com.example.ecommercefurniture.util.Resource
import com.example.ecommercefurniture.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tvDontHaveanAccount.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.apply {
            btnRegisterRegister.setOnClickListener {

                val user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)

              //  findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnRegisterRegister.startAnimation()
                    }

                    is Resource.Success -> {
                        Log.d("test", it.data.toString())

                        lifecycleScope.launch {

                            kotlinx.coroutines.delay(2000)
                            binding.btnRegisterRegister.revertAnimation()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                    }

                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        binding.btnRegisterRegister.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
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