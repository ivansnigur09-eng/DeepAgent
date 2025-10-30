package com.deepagent.launcher.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deepagent.launcher.DeepAgentApplication
import com.deepagent.launcher.R
import com.deepagent.launcher.databinding.ActivityInviteActivationBinding
import com.deepagent.launcher.security.InviteValidator

class InviteActivationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityInviteActivationBinding
    private val secureStorage by lazy { (application as DeepAgentApplication).secureStorage }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInviteActivationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.activateButton.setOnClickListener {
            val inviteCode = binding.inviteCodeEditText.text.toString().trim()
            
            if (inviteCode.isEmpty()) {
                Toast.makeText(this, R.string.enter_invite_code, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (InviteValidator.validateInviteCode(inviteCode)) {
                secureStorage.saveInviteToken(inviteCode)
                Toast.makeText(this, R.string.activation_successful, Toast.LENGTH_SHORT).show()
                
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, R.string.invalid_invite_code, Toast.LENGTH_LONG).show()
            }
        }
    }
}
