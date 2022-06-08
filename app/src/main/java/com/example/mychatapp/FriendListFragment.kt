package com.example.mychatapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychatapp.adapter.UserAdapter
import com.example.mychatapp.adapter.UserClickListener
import com.example.mychatapp.databinding.FragmentFriendListBinding
import com.example.mychatapp.model.User
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FriendListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentFriendListBinding? = null
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
        _binding = FragmentFriendListBinding.inflate(inflater, container, false)
        val view = binding.root

        val query = FirebaseFirestore.getInstance().collection("users")
            .document(Firebase.auth.currentUser!!.uid)
            .collection("friends")
        val config = PagingConfig(20, 10, false)
        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(this)
            .setQuery(query, config, User::class.java)
            .build()

        val adapter = UserAdapter(
            options, UserClickListener {

            }
        )

        binding.rvFriends.adapter = adapter
        binding.rvFriends.layoutManager = LinearLayoutManager(context)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FriendListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}