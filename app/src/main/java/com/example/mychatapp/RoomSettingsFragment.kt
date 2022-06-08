package com.example.mychatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychatapp.adapter.MemberAdapter
import com.example.mychatapp.databinding.FragmentRoomSettingsBinding
import com.example.mychatapp.model.Member
import com.example.mychatapp.model.User
import com.example.mychatapp.utils.deleteMember
import com.example.mychatapp.utils.getRoom
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RoomSettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RoomSettingsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentRoomSettingsBinding? = null
    private val binding get() = _binding!!

    val args: RoomSettingsFragmentArgs by navArgs()

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
        _binding = FragmentRoomSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        val query = FirebaseFirestore.getInstance().collection("rooms")
            .document(args.roomId)
            .collection("membersInfo")
            .orderBy("role")
        val config = PagingConfig(20, 10, false)
        val options = FirestorePagingOptions.Builder<Member>()
            .setLifecycleOwner(this)
            .setQuery(query, config, Member::class.java)
            .build()

        val adapter = MemberAdapter(options)

        binding.rvMembers.adapter = adapter
        binding.rvMembers.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            val room = getRoom(args.roomId)
            binding.tvRoomName.text = room?.name ?: ""
            binding.tvDescription.text = room?.description ?: ""
        }



        binding.layoutLeaveRoom.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                deleteMember(args.roomId, Firebase.auth.currentUser!!.uid)
                findNavController().navigate(RoomSettingsFragmentDirections.actionRoomSettingsFragmentToRoomListFragment())
            }
        }

        binding.layoutAddPeople.setOnClickListener {
            findNavController().navigate(
                RoomSettingsFragmentDirections.actionRoomSettingsFragmentToInviteFriendFragment(args.roomId))
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RoomSettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}