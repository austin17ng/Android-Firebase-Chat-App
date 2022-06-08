package com.example.mychatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mychatapp.databinding.FragmentEditProfileBinding
import com.example.mychatapp.model.User
import com.example.mychatapp.utils.getCurrentUser
import com.example.mychatapp.utils.updateCurrentUser
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        lifecycleScope.launch {
            val me = getCurrentUser()
            binding.etName.setText(me?.displayName ?: "")
            binding.etBio.setText(me?.bio ?: "")
        }

        binding.btnSave.setOnClickListener {
            updateProfile()
        }

        return view
    }

    private fun updateProfile() {
        lifecycleScope.launch {
            val user = User(
                uid = null,
                photoUrl = null,
                displayName = binding.etName.text.toString(),
                nickname = null,
                bio = binding.etBio.text.toString(),
            )
            updateCurrentUser(user)
            Snackbar.make(binding.layoutEditProfile, "Profile updated", Snackbar.LENGTH_LONG).show()
            findNavController().popBackStack()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}