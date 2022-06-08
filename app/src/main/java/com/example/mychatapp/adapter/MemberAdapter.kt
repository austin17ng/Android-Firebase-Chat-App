package com.example.mychatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapp.R
import com.example.mychatapp.databinding.ItemMemberBinding
import com.example.mychatapp.model.Member
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions


class MemberAdapter(options: FirestorePagingOptions<Member>) :
    FirestorePagingAdapter<Member, MemberAdapter.MemberViewHolder>(options) {

    class MemberViewHolder(private val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(member: Member) {
            binding.member = member
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_member, parent, false)
        val binding = ItemMemberBinding.bind(view)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int, model: Member) {
        holder.bind(model)
    }

}
