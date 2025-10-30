package com.deepagent.launcher

import android.app.Application
import com.deepagent.launcher.security.SecureStorage

class DeepAgentApplication : Application() {
    
    lateinit var secureStorage: SecureStorage
        private set
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        secureStorage = SecureStorage(this)
    }
    
    companion object {
        lateinit var instance: DeepAgentApplication
            private set
    }
}
