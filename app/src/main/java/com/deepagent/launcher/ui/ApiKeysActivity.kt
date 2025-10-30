package com.deepagent.launcher.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepagent.launcher.DeepAgentApplication
import com.deepagent.launcher.R
import com.deepagent.launcher.databinding.ActivityApiKeysBinding
import com.google.android.material.textfield.TextInputEditText

class ApiKeysActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityApiKeysBinding
    private lateinit var apiKeysAdapter: ApiKeysAdapter
    private val secureStorage by lazy { (application as DeepAgentApplication).secureStorage }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApiKeysBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        loadApiKeys()
        
        binding.addApiKeyFab.setOnClickListener {
            showAddApiKeyDialog()
        }
    }
    
    private fun setupToolbar() {
        binding.toolbar.title = getString(R.string.api_keys)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        apiKeysAdapter = ApiKeysAdapter(
            onDeleteClick = { service ->
                secureStorage.deleteApiKey(service)
                loadApiKeys()
                Toast.makeText(this, "Deleted $service", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.apiKeysRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ApiKeysActivity)
            adapter = apiKeysAdapter
        }
    }
    
    private fun loadApiKeys() {
        val apiKeys = secureStorage.getAllApiKeys()
        apiKeysAdapter.submitList(apiKeys.toList())
    }
    
    private fun showAddApiKeyDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_api_key, null)
        val serviceInput = dialogView.findViewById<TextInputEditText>(R.id.serviceNameInput)
        val keyInput = dialogView.findViewById<TextInputEditText>(R.id.apiKeyInput)
        
        AlertDialog.Builder(this)
            .setTitle(R.string.add_api_key)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val service = serviceInput.text.toString().trim()
                val key = keyInput.text.toString().trim()
                
                if (service.isNotEmpty() && key.isNotEmpty()) {
                    secureStorage.saveApiKey(service, key)
                    loadApiKeys()
                    Toast.makeText(this, "Saved $service", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
