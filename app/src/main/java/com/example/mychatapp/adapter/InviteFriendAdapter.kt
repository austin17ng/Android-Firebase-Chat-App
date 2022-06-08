package com.example.mychatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapp.R
import com.example.mychatapp.databinding.ItemInviteFriendBinding
import com.example.mychatapp.model.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions

class InviteFriendAdapter(options: FirestorePagingOptions<User>, val inviteListener: InviteListener) :
    FirestorePagingAdapter<User, InviteFriendAdapter.InviteFriendViewHolder>(options) {
    class InviteFriendViewHolder(private val binding: ItemInviteFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, inviteListener: InviteListener) {
            binding.user = user
            binding.inviteListener = inviteListener
            binding.button.setOnClickListener {
                binding.button.text = "Invited"
//                binding.button.setBackgroundResource(R.drawable.round_gray_shape)
                binding.button.background = AppCompatResources.getDrawable(binding.layoutItemInviteFriend.context, R.drawable.round_gray_shape)
//                binding.button.background = it.context.getDrawable(R.drawable.round_gray_shape)
                inviteListener.invite(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteFriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_invite_friend, parent, false)
        val binding = ItemInviteFriendBinding.bind(view)
        return InviteFriendViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: InviteFriendViewHolder,
        position: Int,
        model: User
    ) {
        holder.bind(model, inviteListener)
    }

}

class InviteListener(val inviteListener: (user: User) -> Unit) {
    fun invite(user: User) = inviteListener(user)
}