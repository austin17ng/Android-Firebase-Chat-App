package com.example.mychatapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychatapp.adapter.*
import com.example.mychatapp.databinding.FragmentRegisterBinding
import com.example.mychatapp.databinding.FragmentRequestListBinding
import com.example.mychatapp.model.Room
import com.example.mychatapp.model.User
import com.example.mychatapp.utils.confirmFriend
import com.example.mychatapp.utils.deleteFriendRequest
import com.example.mychatapp.utils.getCurrentUser
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RequestListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentRequestListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FriendRequestAdapter

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
        _binding = FragmentRequestListBinding.inflate(inflater, container, false)
        val view = binding.root

        val query = FirebaseFirestore.getInstance().collection("users")
            .document(Firebase.auth.currentUser!!.uid)
            .collection("friendRequests")
        val config = PagingConfig(20, 10, false)
        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(this)
            .setQuery(query, config, User::class.java)
            .build()

        adapter = FriendRequestAdapter(
            options,
            ConfirmListener {
                lifecycleScope.launch {
                    val me = getCurrentUser()
                    confirmFriend(me!!, it)
                    adapter.refresh()
                }
            },
            DeleteListener {
                lifecycleScope.launch {
                    deleteFriendRequest(Firebase.auth.currentUser!!.uid, it.uid!!)
                    adapter.refresh()
                }
            }
        )
        binding.rvFriendRequests.adapter = adapter
        binding.rvFriendRequests.layoutManager = LinearLayoutManager(context)
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RequestListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RequestListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}