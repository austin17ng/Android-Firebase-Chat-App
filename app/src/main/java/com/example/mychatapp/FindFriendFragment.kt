package com.example.mychatapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mychatapp.databinding.FragmentFindFriendBinding
import com.example.mychatapp.model.User
import com.example.mychatapp.utils.createFriendRequest
import com.example.mychatapp.utils.findUserByNickname
import com.example.mychatapp.utils.getCurrentUser
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FindFriendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FindFriendFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentFindFriendBinding? = null
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
        _binding = FragmentFindFriendBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnSendRequest.setOnClickListener {
            addFriend(binding.etNickname.text.toString().trim())
        }

        return view
    }

    private fun addFriend(nickname: String) {
        lifecycleScope.launch {
            val snapshot = findUserByNickname(nickname)
            val user = User(
                uid = snapshot!!.id,
                nickname = snapshot.getString("nickname"),
                displayName = snapshot.getString("displayName"),
                bio = snapshot.getString("bio"),
                photoUrl = snapshot.getString("photoUrl")
            )
            val me = getCurrentUser()
            createFriendRequest(user.uid!!, me!!)
//            Log.d("xxx", user.displayName!!)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FindFriendFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FindFriendFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}