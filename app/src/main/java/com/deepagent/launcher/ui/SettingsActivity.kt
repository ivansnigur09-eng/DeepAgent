package com.deepagent.launcher.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.deepagent.launcher.BuildConfig
import com.deepagent.launcher.DeepAgentApplication
import com.deepagent.launcher.R
import com.deepagent.launcher.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private val secureStorage by lazy { (application as DeepAgentApplication).secureStorage }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupBiometric()
        setupApiKeys()
        setupVersion()
    }
    
    private fun setupToolbar() {
        binding.toolbar.title = getString(R.string.settings)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupBiometric() {
        binding.biometricSwitch.isChecked = secureStorage.isBiometricEnabled()
        binding.biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            secureStorage.saveBiometricEnabled(isChecked)
        }
    }
    
    private fun setupApiKeys() {
        binding.manageApiKeysButton.setOnClickListener {
            startActivity(Intent(this, ApiKeysActivity::class.java))
        }
    }
    
    private fun setupRevenueDashboard() {
        // Add button in layout if needed
        // For now, accessible from main screen
    }
    
    private fun setupVersion() {
        binding.versionText.text = "${getString(R.string.version)}: ${BuildConfig.VERSION_NAME}"
    }
}
