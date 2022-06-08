package com.example.mychatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapp.R
import com.example.mychatapp.databinding.ItemRoomBinding
import com.example.mychatapp.model.Room
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions

class RoomAdapter(options: FirestorePagingOptions<Room>, val clickListener: RoomClickListener) :
    FirestorePagingAdapter<Room, RoomAdapter.RoomViewHolder>(options) {
    class RoomViewHolder(private val binding: ItemRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(room: Room, clickListener: RoomClickListener) {
                binding.room = room
                binding.clickListener = clickListener
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_room, parent, false)
        val binding = ItemRoomBinding.bind(view)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int, model: Room) {
        holder.bind(model, clickListener)
    }
}

class RoomClickListener(val clickListener: (room: Room) -> Unit) {
    fun onClick(room: Room) = clickListener(room)
}