package com.deepagent.launcher.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepagent.launcher.R
import com.deepagent.launcher.api.models.TradingSignal
import com.deepagent.launcher.databinding.ActivityRevenueDashboardBinding
import com.deepagent.launcher.services.TradingBotService
import kotlinx.coroutines.launch

class RevenueDashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRevenueDashboardBinding
    private lateinit var signalsAdapter: TradingSignalsAdapter
    private var isTradingBotRunning = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRevenueDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupTradingBot()
        observeTradingSignals()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        signalsAdapter = TradingSignalsAdapter()
        binding.signalsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RevenueDashboardActivity)
            adapter = signalsAdapter
        }
    }
    
    private fun setupTradingBot() {
        binding.toggleTradingButton.setOnClickListener {
            if (isTradingBotRunning) {
                TradingBotService.stopBot(this)
                binding.toggleTradingButton.text = "Start Trading Bot"
                isTradingBotRunning = false
            } else {
                TradingBotService.startBot(this)
                binding.toggleTradingButton.text = "Stop Trading Bot"
                isTradingBotRunning = true
            }
        }
    }
    
    private fun observeTradingSignals() {
        lifecycleScope.launch {
            TradingBotService.getInstance()?.tradingSignals?.collect { signal ->
                addSignal(signal)
                updateStats(signal)
            }
        }
    }
    
    private fun addSignal(signal: TradingSignal) {
        val currentSignals = signalsAdapter.currentList.toMutableList()
        currentSignals.add(0, signal)
        signalsAdapter.submitList(currentSignals.take(20)) // Keep last 20 signals
        
        binding.tradingSignalsText.text = currentSignals.size.toString()
    }
    
    private fun updateStats(signal: TradingSignal) {
        // Update profit (simplified)
        val currentProfit = binding.tradingProfitText.text.toString()
            .replace("$", "")
            .toDoubleOrNull() ?: 0.0
        
        val newProfit = currentProfit + (signal.expectedProfit * 100) // Assuming $100 per trade
        binding.tradingProfitText.text = "$${"%.2f".format(newProfit)}"
        
        // Update total revenue
        binding.totalRevenueText.text = "$${"%.2f".format(newProfit)}"
    }
}
