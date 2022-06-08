package com.example.mychatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapp.R
import com.example.mychatapp.databinding.ItemFriendMessageBinding
import com.example.mychatapp.databinding.ItemMyMessageBinding
import com.example.mychatapp.model.Message
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MessageAdapter(private val options: FirestoreRecyclerOptions<Message>) :
    FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {
    class MyMessageViewHolder(private val binding: ItemMyMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.message = message
            binding.tvMessage.setOnClickListener {
                if (binding.tvSendTime.visibility == View.VISIBLE) {
                    binding.tvSendTime.visibility = View.GONE
                } else {
                    binding.tvSendTime.visibility = View.VISIBLE
                }
            }
        }
    }

    class FriendMessageViewHolder(private val binding: ItemFriendMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.message = message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val uid = options.snapshots[position].uid
        return if (uid != null && uid.equals(Firebase.auth.currentUser!!.uid)) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_FRIEND_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEW_TYPE_FRIEND_MESSAGE) {
            val view = inflater.inflate(R.layout.item_friend_message, parent, false)
            val binding = ItemFriendMessageBinding.bind(view)
            return FriendMessageViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.item_my_message, parent, false)
            val binding = ItemMyMessageBinding.bind(view)
            return MyMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        val uid = options.snapshots[position].uid
        if (uid != null && uid.equals(Firebase.auth.currentUser!!.uid)) {
            (holder as MyMessageViewHolder).bind(model)
        } else {
            (holder as FriendMessageViewHolder).bind(model)
        }
    }

    companion object {
        const val VIEW_TYPE_FRIEND_MESSAGE = 0
        const val VIEW_TYPE_MY_MESSAGE = 1
    }
}