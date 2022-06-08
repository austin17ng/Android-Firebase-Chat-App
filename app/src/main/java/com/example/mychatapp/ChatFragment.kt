package com.example.mychatapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.mychatapp.adapter.MessageAdapter
import com.example.mychatapp.databinding.FragmentChatBinding
import com.example.mychatapp.model.Message
import com.example.mychatapp.utils.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    val args: ChatFragmentArgs by navArgs()

    private lateinit var adapter: MessageAdapter

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
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = args.roomName
        setHasOptionsMenu(true)

        val query = FirebaseFirestore.getInstance()
            .collection("rooms")
            .document(args.roomId)
            .collection("chats")
            .orderBy("sendTimestamp")
        val options = FirestoreRecyclerOptions.Builder<Message>()
            .setLifecycleOwner(this)
            .setQuery(query, Message::class.java)
            .build()

        adapter = MessageAdapter(options)
//        val layoutManager = LinearLayoutManager(context)
        val layoutManager = WrapContentLinearLayoutManager(activity)
        layoutManager.stackFromEnd = true
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = layoutManager
        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(binding.rvMessages, adapter, layoutManager)
        )

        binding.btnSend.setOnClickListener {
            sendTextMessage()
        }

        binding.btnGallery.setOnClickListener {
            openGalleryForImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_chat, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                findNavController().navigate(
                    ChatFragmentDirections.actionChatFragmentToRoomSettingsFragment(
                        args.roomId
                    )
                )
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            if (data != null) {
                data.data?.let { sendImageMessage(it) }
            }
        }
    }


    private fun sendTextMessage() {
        val text = binding.etInput.text.toString().trim()
        lifecycleScope.launch {
            createMessage(args.roomId, text, null)
            binding.etInput.setText("")
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    private fun sendImageMessage(uri: Uri) {
        lifecycleScope.launch {
            val uid = Firebase.auth.currentUser!!.uid
            val messageRef = createMessage(args.roomId, null, null)
            val task = uploadFile(uri, "rooms/${args.roomId}/$uid-${Timestamp.now().seconds}")
            val uri = task!!.metadata!!.reference!!.downloadUrl.await()

            messageRef!!.update("imageUrl", uri.toString()).await()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

class WrapContentLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
//            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
    }
}