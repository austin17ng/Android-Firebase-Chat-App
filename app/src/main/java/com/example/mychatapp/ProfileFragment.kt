package com.example.mychatapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mychatapp.databinding.FragmentProfileBinding
import com.example.mychatapp.utils.getCurrentUser
import com.example.mychatapp.utils.uploadFile
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        onImageSelected(uri)
    }

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
        setHasOptionsMenu(true)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        lifecycleScope.launch {
            val user = getCurrentUser()
            binding.tvName.text = user?.displayName ?: ""
            binding.tvNickname.text = "@".plus(user?.nickname) ?: ""
            binding.tvBio.text = user?.bio ?: ""
            if (user != null) {
                Glide.with(requireContext())
                    .load(user.photoUrl)
                    .into(binding.imgAccount)
            }
        }

        binding.imgAccount.setOnClickListener {
            getContent.launch("image/*")
        }

        return view
    }

    private fun onImageSelected(uri: Uri?) {
        if (uri != null) {
            updatePhoto(uri)
        }
    }

    private fun updatePhoto(uri: Uri) {
        lifecycleScope.launch {
            val uid = Firebase.auth.currentUser!!.uid
            val time = Timestamp.now()
            val task = uploadFile(uri, "users/$uid/avatar_${time.seconds}")
            val url = task!!.metadata!!.reference!!.downloadUrl.await()


            Firebase.firestore.collection("users")
                .document(uid)
                .update(
                    mapOf("photoUrl" to url.toString())
                ).await()


            Glide.with(requireContext())
                .load(url)
                .into(binding.imgAccount)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_account, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                Firebase.auth.signOut()
                val intent = Intent(context, LoginActivity::class.java)
                requireActivity().startActivity(intent)
                true
            }
            R.id.action_edit_profile -> {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}