package com.example.mychatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapp.R
import com.example.mychatapp.databinding.ItemUserBinding
import com.example.mychatapp.model.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions


class UserAdapter(options: FirestorePagingOptions<User>, val userClickListener: UserClickListener) :
    FirestorePagingAdapter<User, UserAdapter.UserViewHolder>(options) {

    class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, userClickListener: UserClickListener) {
            binding.user = user
            binding.onClickListener = userClickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_user, parent, false)
        val binding = ItemUserBinding.bind(view)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
        holder.bind(model, userClickListener)
    }
}

class UserClickListener(val userClickListener: (user: User) -> Unit) {
    fun onClick(user: User) = userClickListener(user)
}