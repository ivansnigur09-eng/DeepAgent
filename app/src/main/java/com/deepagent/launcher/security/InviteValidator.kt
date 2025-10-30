package com.deepagent.launcher.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

object InviteValidator {
    
    // Embedded RSA public key for JWT verification
    // This should match the private key used in invite_token_generator.py
    private const val PUBLIC_KEY_PEM = """
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy8Dbv8prpJ/0kKhlGeJY
ozo2t60EG8EcYTlHdgHW6MRm0n3xq8UNluksGB6eDdPCvhpJ3egpx3gQzaL4DZHe
rY8O7NOcoU5y9fKopWdE2BTziOCXsN3FPz24S0r35L4Lwnjh1+b+roB1XGXF4oR5
xXAx7u2LHfeW7cVp0q3+3qXMQEdwHQ6mf/H9B/xMxKZuATCWpnnvWneaPwYfzHOv
tWqVmRSPoK4OZr0VJ0M7VKYWcZ5t6Qlr3s0+g8IQFWX7qVqvKD1CjneZWfVLUj6O
7PfkcKA9vBoihnqyNfPgKoiFNRwAKyfAVpbMO40+W7yvwhquN0qKhyQ3P9KF1+XF
dQIDAQAB
-----END PUBLIC KEY-----
"""
    
    fun validateInviteCode(token: String): Boolean {
        return try {
            val publicKey = getPublicKey()
            val algorithm = Algorithm.RSA256(publicKey, null)
            val verifier = JWT.require(algorithm)
                .withIssuer("deepagent")
                .build()
            
            val decodedJWT = verifier.verify(token)
            
            // Check if token has expired
            val expiresAt = decodedJWT.expiresAt
            if (expiresAt != null && expiresAt.time < System.currentTimeMillis()) {
                return false
            }
            
            true
        } catch (e: JWTVerificationException) {
            false
        } catch (e: Exception) {
            false
        }
    }
    
    private fun getPublicKey(): RSAPublicKey {
        val publicKeyPEM = PUBLIC_KEY_PEM
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")
        
        val decoded = Base64.getDecoder().decode(publicKeyPEM)
        val spec = X509EncodedKeySpec(decoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(spec) as RSAPublicKey
    }
}
