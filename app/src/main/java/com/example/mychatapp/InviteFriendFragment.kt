package com.example.mychatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychatapp.adapter.InviteFriendAdapter
import com.example.mychatapp.adapter.InviteListener
import com.example.mychatapp.adapter.UserAdapter
import com.example.mychatapp.adapter.UserClickListener
import com.example.mychatapp.databinding.FragmentInviteFriendBinding
import com.example.mychatapp.model.User
import com.example.mychatapp.utils.addMember
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InviteFriendFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentInviteFriendBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    val args: InviteFriendFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInviteFriendBinding.inflate(inflater, container, false)
        val view = binding.root

        val query = FirebaseFirestore.getInstance().collection("users")
            .document(Firebase.auth.currentUser!!.uid)
            .collection("friends")
        val config = PagingConfig(20, 10, false)
        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(this)
            .setQuery(query, config, User::class.java)
            .build()

        val adapter = InviteFriendAdapter(
            options, InviteListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    addMember(args.roomId, it.uid!!)

                }
            }
        )

        binding.rvFriends.adapter = adapter
        binding.rvFriends.layoutManager = LinearLayoutManager(context)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InviteFriendFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InviteFriendFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}