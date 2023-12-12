package com.example.ecommercefurniture.fragments.shopping

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ecommercefurniture.Activities.LoginRegisterActivity
import com.example.ecommercefurniture.BuildConfig
import com.example.ecommercefurniture.R
import com.example.ecommercefurniture.databinding.FragmentProfileBinding
import com.example.ecommercefurniture.util.Resource
import com.example.ecommercefurniture.util.showBottomNavigationView
import com.example.ecommercefurniture.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ProfileFragment: Fragment()  {
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment3_to_userAccountFragment)
        }
        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment3_to_allOrdersFragment)
        }
        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragment3ToBillingFragment(0f,
                emptyArray(), false)
            findNavController().navigate(action)
        }
        binding.linearLogOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath).error(ColorDrawable(
                            Color.BLACK)).into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                    }
                    is Resource.Error -> {

                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }
}