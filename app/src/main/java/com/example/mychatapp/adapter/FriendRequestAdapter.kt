package com.example.mychatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapp.R
import com.example.mychatapp.databinding.ItemFriendRequestBinding
import com.example.mychatapp.model.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions

class FriendRequestAdapter(options: FirestorePagingOptions<User>, val confirmListener: ConfirmListener, val deleteListener: DeleteListener) :
    FirestorePagingAdapter<User, FriendRequestAdapter.FriendRequestViewHolder>(options) {
    class FriendRequestViewHolder(private val binding: ItemFriendRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, confirmListener: ConfirmListener, deleteListener: DeleteListener) {
            binding.user = user
            binding.confirm = confirmListener
            binding.delete = deleteListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_friend_request, parent, false)
        val binding = ItemFriendRequestBinding.bind(view)
        return FriendRequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int, model: User) {
        holder.bind(model, confirmListener, deleteListener)
    }
}

class ConfirmListener(val confirmListener: (user: User) -> Unit) {
    fun confirm(user: User) = confirmListener(user)
}

class DeleteListener(val deleteListener: (user: User) -> Unit) {
    fun delete(user: User) = deleteListener(user)
}
