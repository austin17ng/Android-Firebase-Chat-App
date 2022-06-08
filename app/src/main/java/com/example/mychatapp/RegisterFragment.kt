package com.example.mychatapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mychatapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegisterFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentRegisterBinding? = null
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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnNext.setOnClickListener {
            createAccount(
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString().trim()
            )
        }

        return view
    }

    private fun createAccount(email: String, password: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val auth = Firebase.auth
            auth.createUserWithEmailAndPassword(email, password).await()
            val currentUser = auth.currentUser
            Firebase.firestore.collection("users")
                .document(currentUser!!.uid)
                .set(
                    mapOf(
                        "nickname" to null,
                        "displayName" to null,
                        "bio" to null,
                        "photoUrl" to null
                    )
                ).await()
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToChooseNicknameFragment())
        }

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}