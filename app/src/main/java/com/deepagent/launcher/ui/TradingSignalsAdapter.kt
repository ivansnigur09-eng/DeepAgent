package com.deepagent.launcher.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deepagent.launcher.api.models.TradingSignal
import com.deepagent.launcher.databinding.ItemTradingSignalBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TradingSignalsAdapter : ListAdapter<TradingSignal, TradingSignalsAdapter.SignalViewHolder>(SignalDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignalViewHolder {
        val binding = ItemTradingSignalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SignalViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: SignalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class SignalViewHolder(
        private val binding: ItemTradingSignalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        
        fun bind(signal: TradingSignal) {
            binding.signalType.text = signal.type
            binding.symbolText.text = signal.symbol
            binding.priceText.text = "$${"%.2f".format(signal.price)}"
            binding.profitText.text = "+${"%.2f".format(signal.expectedProfit)}%"
            binding.reasonText.text = signal.reason
            binding.confidenceText.text = "Confidence: ${(signal.confidence * 100).toInt()}%"
            binding.timeText.text = dateFormat.format(Date(signal.timestamp))
        }
    }
    
    private class SignalDiffCallback : DiffUtil.ItemCallback<TradingSignal>() {
        override fun areItemsTheSame(oldItem: TradingSignal, newItem: TradingSignal): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }
        
        override fun areContentsTheSame(oldItem: TradingSignal, newItem: TradingSignal): Boolean {
            return oldItem == newItem
        }
    }
}
