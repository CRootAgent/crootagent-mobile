package com.crootagent.mobile.ui.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crootagent.mobile.R
import com.crootagent.mobile.data.model.ChatMessage
import com.crootagent.mobile.databinding.ItemChatMessageBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(DiffCallback) {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            val context = binding.root.context
            val isUser = message.role.equals("user", ignoreCase = true)

            // Adjust alignment and layout gravity
            val parentLayout = binding.root as? LinearLayout
            if (isUser) {
                parentLayout?.gravity = Gravity.END
                binding.bubbleContainer.setBackgroundResource(R.drawable.bg_chat_user)
                binding.tvRole.text = "You"
                binding.tvRole.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.tvModel.visibility = View.GONE
                binding.tvMessageContent.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.tvTimestamp.setTextColor(ContextCompat.getColor(context, R.color.slate_900))
            } else {
                parentLayout?.gravity = Gravity.START
                binding.bubbleContainer.setBackgroundResource(R.drawable.bg_chat_assistant)
                binding.tvRole.text = "CRootAgent"
                binding.tvRole.setTextColor(ContextCompat.getColor(context, R.color.accent_cyan_light))
                binding.tvModel.visibility = if (!message.model.isNullOrEmpty()) View.VISIBLE else View.GONE
                binding.tvModel.text = message.model?.let { "($it)" } ?: ""
                binding.tvMessageContent.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.tvTimestamp.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            }

            binding.tvMessageContent.text = message.content
            binding.tvTimestamp.text = timeFormat.format(Date(message.timestamp))
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
                return oldItem == newItem
            }
        }
    }
}
