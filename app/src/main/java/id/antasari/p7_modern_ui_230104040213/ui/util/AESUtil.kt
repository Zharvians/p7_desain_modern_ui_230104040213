// app/src/main/java/id/antasari/p7_modern_ui_230104040213/util/AESUtil.kt
package id.antasari.p7_modern_ui_230104040213.ui.util

import android.util.Base64
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.KeyGenerator

object AESUtil {
    private const val KEY_SIZE = 256
    private const val GCM_TAG_LEN = 128
    private const val IV_SIZE = 12 // recommended 12 bytes for GCM

    fun generateKey(): ByteArray {
        val kg = KeyGenerator.getInstance("AES")
        kg.init(KEY_SIZE)
        val key = kg.generateKey()
        return key.encoded
    }

    fun keyFromBytes(bytes: ByteArray): SecretKey = SecretKeySpec(bytes, "AES")

    // Encrypt: returns base64(iv + ciphertext)
    fun encrypt(plain: ByteArray, key: SecretKey): String {
        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_LEN, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        val cipherText = cipher.doFinal(plain)
        val bb = ByteBuffer.allocate(iv.size + cipherText.size)
        bb.put(iv)
        bb.put(cipherText)
        return Base64.encodeToString(bb.array(), Base64.NO_WRAP)
    }

    // Decrypt from base64(iv + ciphertext)
    fun decrypt(base64: String, key: SecretKey): ByteArray {
        val all = Base64.decode(base64, Base64.NO_WRAP)
        val iv = all.copyOfRange(0, IV_SIZE)
        val cipherText = all.copyOfRange(IV_SIZE, all.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_LEN, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(cipherText)
    }
}
