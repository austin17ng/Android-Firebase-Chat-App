package com.example.mychatapp.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapp.adapter.MessageAdapter

class ScrollToBottomObserver(
    private val recyclerView: RecyclerView,
    private val adapter: MessageAdapter,
    private val layoutManager: LinearLayoutManager
) : RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        val count = adapter.itemCount
        val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

        val loading = lastVisiblePosition == -1
        val atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
        if (loading || atBottom) {
            recyclerView.scrollToPosition(positionStart)
        }
    }
}