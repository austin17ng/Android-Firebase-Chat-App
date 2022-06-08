package com.example.mychatapp.utils

import android.text.format.DateUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (url != null) {
        Glide.with(view.context)
            .load(url)
            .into(view)
    }
}

@BindingAdapter("timestamp")
fun formatTimestamp(textView: TextView, timestamp: Timestamp?) {
    if (timestamp != null) {
        val text = if (DateUtils.isToday(timestamp.toDate().time)) {
            SimpleDateFormat("HH:mm").format(timestamp.toDate())
        } else {
            SimpleDateFormat("dd-MM-yyy HH:mm").format(timestamp.toDate())
        }
        textView.setText(text)
    }
}