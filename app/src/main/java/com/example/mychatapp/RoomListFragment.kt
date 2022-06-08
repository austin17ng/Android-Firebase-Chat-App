package com.example.mychatapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychatapp.adapter.RoomAdapter
import com.example.mychatapp.adapter.RoomClickListener
import com.example.mychatapp.databinding.FragmentRoomListBinding
import com.example.mychatapp.model.Room
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RoomListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentRoomListBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
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
        _binding = FragmentRoomListBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.fabCreateRoom.setOnClickListener {
            findNavController().navigate(RoomListFragmentDirections.actionRoomListFragmentToCreateRoomFragment())
        }


        val query = FirebaseFirestore.getInstance().collection("rooms")
            .whereArrayContains("members", Firebase.auth.currentUser!!.uid)
//            .orderBy("name", Query.Direction.DESCENDING)

        val config = PagingConfig(20, 10, false)
        val options = FirestorePagingOptions.Builder<Room>()
            .setLifecycleOwner(this)
            .setQuery(query, config) {
                Room(
                    it.id,
                    it.getString("name"),
                    it.getString("description"),
                    it.getString("photoUrl"),
                    it.getString("lastMessage"),
                    it.getTimestamp("lastMessageTime")
                )
            }
            .build()

        val adapter = RoomAdapter(options, RoomClickListener {
            Log.d("xxx", it.id.toString())
            findNavController().navigate(
                RoomListFragmentDirections.actionRoomListFragmentToChatFragment(
                    it.id!!, it.name!!
                )
            )
        })
        binding.rvRooms.adapter = adapter
        binding.rvRooms.layoutManager = LinearLayoutManager(context)


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RoomListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RoomListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}