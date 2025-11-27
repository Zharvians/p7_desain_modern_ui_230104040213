package id.antasari.p7_modern_ui_230104040213

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import id.antasari.p7_modern_ui_230104040213.ui.account.AccountStorage
import id.antasari.p7_modern_ui_230104040213.ui.auth.AuthViewModel
import id.antasari.p7_modern_ui_230104040213.ui.auth.SecureAuthApp
import id.antasari.p7_modern_ui_230104040213.ui.theme.P7ModernUiTheme
import id.antasari.p7_modern_ui_230104040213.ui.util.BiometricUtils
import id.antasari.p7_modern_ui_230104040213.ui.util.DeviceSecurity
import id.antasari.p7_modern_ui_230104040213.ui.util.IntruderAlert
import id.antasari.p7_modern_ui_230104040213.ui.util.AESUtil
import javax.crypto.SecretKey

class MainActivity : FragmentActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val authViewModel: AuthViewModel by viewModels()

    private var lastBackgroundAt: Long? = null
    private val APP_LOCK_TIMEOUT_MS = 30_000L

    private var failedAttempts = 0
    private val MAX_FAILED_ATTEMPTS = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Cek biometric
        val isBiometricReady = BiometricUtils.isBiometricReady(this)
        authViewModel.setBiometricAvailability(isBiometricReady)

        // 🔐 2. Tambahan — cek device integrity
        val isRooted = DeviceSecurity.isDeviceRooted()
        if (isRooted) {
            authViewModel.setBiometricAvailability(false)
            Toast.makeText(this, "Perangkat terdeteksi root — biometric dinonaktifkan", Toast.LENGTH_LONG).show()
        }

        if (DeviceSecurity.isVpnActive(this)) {
            Toast.makeText(this, "VPN aktif — beberapa fitur mungkin dibatasi", Toast.LENGTH_SHORT).show()
        }

        if (DeviceSecurity.isLikelyPublicWifi(this)) {
            Toast.makeText(this, "Terdeteksi jaringan WiFi publik — tetap hati-hati", Toast.LENGTH_LONG).show()
        }

        // 3. Load akun + device ID check
        val storedAccount = AccountStorage.loadAccount(this)
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        storedAccount?.let {
            val isNewDevice = it.deviceId != deviceId
            authViewModel.restoreAccountFromStorage(
                account = it,
                biometricAvailable = isBiometricReady,
                isNewDevice = isNewDevice
            )

            if (isNewDevice && it.newDeviceAlertsEnabled) {
                Toast.makeText(this, "Login dari perangkat baru terdeteksi", Toast.LENGTH_LONG).show()
            }
        }

        // 4. Biometric prompt
        setupBiometricPrompt()
        preparePromptInfo()

        // 5. Compose
        setContent {
            val uiState by authViewModel.uiState.collectAsState()
            P7ModernUiTheme(darkTheme = uiState.isDarkTheme) {
                SecureAuthApp(
                    authViewModel = authViewModel,
                    onBiometricClick = { triggerBiometricAuth() }
                )
            }
        }
    }

    /** BIOMETRIC SETUP **/
    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    failedAttempts = 0
                    authViewModel.onBiometricAuthenticated()

                    val state = authViewModel.uiState.value

                    // Simpan akun jika baru
                    if (state.registeredEmail.isNullOrBlank()) {
                        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                        AccountStorage.saveAccount(
                            context = this@MainActivity,
                            name = state.name,
                            email = state.registeredEmail ?: state.email,
                            password = state.registeredPassword ?: state.password,
                            biometricEnabled = state.isBiometricEnabled,
                            isDarkTheme = state.isDarkTheme,
                            appLockEnabled = state.appLockEnabled,
                            loginAlertsEnabled = state.loginAlertsEnabled,
                            newDeviceAlertsEnabled = state.newDeviceAlertsEnabled,
                            publicWifiWarningEnabled = state.publicWifiWarningEnabled,
                            deviceId = deviceId
                        )
                    }

                    if (state.loginAlertsEnabled) {
                        Toast.makeText(this@MainActivity, "Login biometric sukses 🎉", Toast.LENGTH_SHORT).show()
                    }

                    // 🔐 Tambahan — AES secure session token
                    val keyBytes = AESUtil.generateKey()
                    val secretKey: SecretKey = AESUtil.keyFromBytes(keyBytes)

                    val token = "session_token_example_${System.currentTimeMillis()}"
                    val encrypted = AESUtil.encrypt(token.toByteArray(Charsets.UTF_8), secretKey)

                    val prefs = getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("session_token_enc", encrypted).apply()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    authViewModel.onBiometricError(errString.toString())

                    Toast.makeText(
                        this@MainActivity,
                        "Biometrik error: $errString",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (errorCode == BiometricPrompt.ERROR_LOCKOUT ||
                        errorCode == BiometricPrompt.ERROR_LOCKOUT_PERMANENT
                    ) {
                        authViewModel.forceLock()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    failedAttempts++

                    Toast.makeText(
                        this@MainActivity,
                        "Biometrik tidak cocok ($failedAttempts/3)",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 🔐 Tambahan — intruder photo jika gagal >= 3
                    if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                        authViewModel.forceLock()

                        Toast.makeText(
                            this@MainActivity,
                            "Terlalu banyak percobaan gagal. Foto intruder diambil.",
                            Toast.LENGTH_LONG
                        ).show()

                        if (ActivityCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            IntruderAlert.capturePhoto(this@MainActivity) { file ->
                                file?.let {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Intruder photo saved: ${it.absolutePath}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                this@MainActivity,
                                arrayOf(Manifest.permission.CAMERA),
                                1234
                            )
                        }
                    }
                }
            }
        )
    }

    private fun preparePromptInfo() {
        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login dengan Biometrik")
            .setSubtitle("Gunakan sidik jari / wajah")
            .setNegativeButtonText("Pakai password")
            .setAllowedAuthenticators(authenticators)
            .build()
    }

    private fun triggerBiometricAuth() {
        val state = authViewModel.uiState.value
        val ready = BiometricUtils.isBiometricReady(this)

        if (!state.isBiometricEnabled || !ready) {
            Toast.makeText(this, "Biometrik tidak aktif / tidak siap", Toast.LENGTH_SHORT).show()
            return
        }

        biometricPrompt.authenticate(promptInfo)
    }

    override fun onStop() {
        super.onStop()
        val state = authViewModel.uiState.value

        if (state.isSignedIn && state.appLockEnabled) {
            lastBackgroundAt = System.currentTimeMillis()
        }
    }

    override fun onStart() {
        super.onStart()
        maybeLockApp()
    }

    private fun maybeLockApp() {
        val last = lastBackgroundAt ?: return
        val state = authViewModel.uiState.value
        if (!state.appLockEnabled || !state.isSignedIn) return

        val elapsed = System.currentTimeMillis() - last

        if (elapsed >= APP_LOCK_TIMEOUT_MS) {
            authViewModel.forceLock()
            lastBackgroundAt = null

            if (state.isBiometricEnabled && BiometricUtils.isBiometricReady(this)) {
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }
}
