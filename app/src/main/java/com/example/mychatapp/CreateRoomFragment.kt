package com.example.mychatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mychatapp.databinding.FragmentCreateRoomBinding
import com.example.mychatapp.utils.createRoom
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CreateRoomFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentCreateRoomBinding? = null
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
        _binding = FragmentCreateRoomBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnCreateRoom.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                createRoom(
                    binding.etName.text.toString().trim(),
                    binding.etDesciption.text.toString().trim(),
                )
                Snackbar.make(binding.layoutCreateRoom, "Room created", Snackbar.LENGTH_LONG)
                    .show()
                findNavController().popBackStack()
            }
        }

        return view

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateRoomFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}