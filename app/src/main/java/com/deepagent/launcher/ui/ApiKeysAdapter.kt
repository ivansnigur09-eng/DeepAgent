package com.deepagent.launcher.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepagent.launcher.databinding.ItemApiKeyBinding

class ApiKeysAdapter(
    private val onDeleteClick: (String) -> Unit
) : ListAdapter<Pair<String, String>, ApiKeysAdapter.ApiKeyViewHolder>(ApiKeyDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiKeyViewHolder {
        val binding = ItemApiKeyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ApiKeyViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ApiKeyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ApiKeyViewHolder(
        private val binding: ItemApiKeyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: Pair<String, String>) {
            val (service, key) = item
            binding.serviceName.text = service
            binding.apiKeyPreview.text = "••••${key.takeLast(4)}"
            binding.deleteButton.setOnClickListener {
                onDeleteClick(service)
            }
        }
    }
    
    private class ApiKeyDiffCallback : DiffUtil.ItemCallback<Pair<String, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>
        ): Boolean {
            return oldItem.first == newItem.first
        }
        
        override fun areContentsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>
        ): Boolean {
            return oldItem == newItem
        }
    }
}
