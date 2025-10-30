package com.deepagent.launcher.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.deepagent.launcher.DeepAgentApplication
import com.deepagent.launcher.databinding.ActivityMainBinding
import com.deepagent.launcher.utils.AppManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var appsAdapter: AppsAdapter
    private val secureStorage by lazy { (application as DeepAgentApplication).secureStorage }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if activated
        if (!secureStorage.isActivated()) {
            startActivity(Intent(this, InviteActivationActivity::class.java))
            finish()
            return
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        loadApps()
        setupSearch()
        
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
    
    private fun setupRecyclerView() {
        appsAdapter = AppsAdapter { appInfo ->
            AppManager.launchApp(this, appInfo.packageName)
        }
        
        binding.appsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 4)
            adapter = appsAdapter
        }
    }
    
    private fun loadApps() {
        val apps = AppManager.getInstalledApps(this)
        appsAdapter.submitList(apps)
        updateEmptyState(apps.isEmpty())
    }
    
    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                val allApps = AppManager.getInstalledApps(this@MainActivity)
                val filteredApps = AppManager.filterApps(allApps, query)
                appsAdapter.submitList(filteredApps)
                updateEmptyState(filteredApps.isEmpty())
            }
        })
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.appsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    override fun onResume() {
        super.onResume()
        loadApps()
    }
}
