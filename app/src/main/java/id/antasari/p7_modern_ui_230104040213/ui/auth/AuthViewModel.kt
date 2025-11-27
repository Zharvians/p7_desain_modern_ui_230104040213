package id.antasari.p7_modern_ui_230104040213.ui.auth

import androidx.lifecycle.ViewModel
import id.antasari.p7_modern_ui_230104040213.ui.account.StoredAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.content.Context


/**
 * State utama untuk layar autentikasi (Login & Create Account)
 */
data class AuthUIState(
    // Data input form
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val deviceId: String = "",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isSignedIn: Boolean = false,
    val isNewDevice: Boolean = false,


    // Biometric
    val isBiometricAvailable: Boolean = false,
    val isBiometricEnabled: Boolean = false,

    // Kredensial terdaftar (hasil CreateAccount / load storage)
    val registeredEmail: String? = null,
    val registeredPassword: String? = null,

    // Theme
    val isDarkTheme: Boolean = false,

    // App lock
    val appLockEnabled: Boolean = true,

    // Security alert toggles
    val loginAlertsEnabled: Boolean = true,
    val newDeviceAlertsEnabled: Boolean = true,
    val publicWifiWarningEnabled: Boolean = false,

    // Pesan error terakhir (jika ada)
    val lastErrorMessage: String? = null
)

private const val PREF_NAME = "auth_prefs"

private const val KEY_NAME = "name"
private const val KEY_EMAIL = "email"
private const val KEY_PASSWORD = "password"
private const val KEY_BIOMETRIC = "biometric_enabled"
private const val KEY_DARK_THEME = "dark_theme"
private const val KEY_APP_LOCK = "app_lock"
private const val KEY_LOGIN_ALERTS = "login_alerts"
private const val KEY_NEW_DEVICE_ALERTS = "new_device_alerts"
private const val KEY_WIFI_WARNING = "wifi_warning"


/**
 * ViewModel untuk mengelola logika autentikasi dan pengaturan keamanan
 */
class AuthViewModel : ViewModel() {

    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_BIOMETRIC = "biometric_enabled"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_APP_LOCK = "app_lock"
        private const val KEY_LOGIN_ALERTS = "login_alerts"
        private const val KEY_NEW_DEVICE_ALERTS = "new_device_alerts"
        private const val KEY_WIFI_WARNING = "wifi_warning"
    }


    private val _uiState = MutableStateFlow(AuthUIState())
    val uiState: StateFlow<AuthUIState> = _uiState.asStateFlow()

    // --- Helper Internal ---
    private fun updateState(block: (AuthUIState) -> AuthUIState) {
        _uiState.value = block(_uiState.value)
    }

    // --- Input handler (dipanggil dari UI) ---
    fun onNameChange(newName: String) {
        updateState { it.copy(name = newName, lastErrorMessage = null) }
    }

    fun onEmailChange(newEmail: String) {
        updateState { it.copy(email = newEmail, lastErrorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        updateState { it.copy(password = newPassword, lastErrorMessage = null) }
    }

    fun onConfirmPasswordChange(newPassword: String) {
        updateState { it.copy(confirmPassword = newPassword, lastErrorMessage = null) }
    }

    fun clearError() {
        updateState { it.copy(lastErrorMessage = null) }
    }

    // --- Login dengan password ---
    fun signInWithPassword(onSuccess: () -> Unit) {
        val current = _uiState.value

        // 1. Harus ada akun terdaftar
        if (current.registeredEmail.isNullOrBlank() || current.registeredPassword.isNullOrBlank()) {
            updateState {
                it.copy(
                    isLoading = false,
                    lastErrorMessage = "Belum ada akun terdaftar. Silakan buat akun terlebih dahulu."
                )
            }
            return
        }

        // 2. Input tidak boleh kosong
        if (current.email.isBlank() || current.password.isBlank()) {
            updateState {
                it.copy(
                    isLoading = false,
                    lastErrorMessage = "Email dan password tidak boleh kosong."
                )
            }
            return
        }
        // 3. Validasi: hanya akun yang terdaftar yang boleh login
        if (current.email != current.registeredEmail ||
            current.password != current.registeredPassword
        ) {
            updateState {
                it.copy(
                    isLoading = false,
                    lastErrorMessage = "Email atau password tidak sesuai dengan akun yang terdaftar."
                )
            }
            return
        }

        // 4. Sukses Login
        updateState {
            it.copy(
                isLoading = false,
                isAuthenticated = true,
                lastErrorMessage = null
            )
        }

        onSuccess()
    }

// ----------------- Create Account / Register + persiapan biometric -----------------

    /**
     * create account:
     * - validasi nama, email, password, konfirmasi
     * - menyimpan ke state
     * - mengaktifkan biometric untuk akun ini (flag)
     * - menyimpan registeredEmail & registeredPassword (untuk validasi Login)
     *
     * return true jika registrasi valid, false jika error.
     */
    fun createAccount(): Boolean {
        val current = _uiState.value

        if (
            current.name.isBlank() ||
            current.email.isBlank() ||
            current.password.isBlank() ||
            current.confirmPassword.isBlank()
        ) {
            updateState {
                it.copy(
                    lastErrorMessage = "Nama, email, password, dan konfirmasi password wajib diisi."
                )
            }
            return false
        }
        if (current.password != current.confirmPassword) {
            updateState {
                it.copy(
                    lastErrorMessage = "Konfirmasi password tidak sama."
                )
            }
            return false
        }

        updateState {
            it.copy(
                isAuthenticated = false,
                isBiometricEnabled = true,
                lastErrorMessage = null,
                registeredEmail = current.email,
                registeredPassword = current.password
            )
        }

        return true
    }

    // --------- Logout & Clear Account ---------
    fun signOut() {
        updateState {
            it.copy(
                isAuthenticated = false,
                password = "",
                confirmPassword = "",
                lastErrorMessage = null
                // registeredEmail & registeredPassword dibiarkan,
                // supaya akun tetap ada selama belum di-delete dari storage
            )
        }
    }

    fun clearAccount() {
        updateState {
            AuthUIState(
                isBiometricAvailable = it.isBiometricAvailable // tetap baca dari device
            )
        }
    }

    // --------- Biometric: state & callback ---------
    fun setBiometricAvailability(available: Boolean) {
        updateState {
            it.copy(isBiometricAvailable = available)
        }
    }
    // app/src/main/java/id/antasari/p7_modern_ui_230104040213/ui/auth/AuthViewModel.kt (Lanjutan)

    fun setBiometricEnabled(enabled: Boolean) {
        updateState {
            it.copy(isBiometricEnabled = enabled)
        }
    }

    fun onBiometricAuthenticated() {
        updateState {
            it.copy(
                isAuthenticated = true,
                lastErrorMessage = null
            )
        }
    }

    fun onBiometricError(message: String) {
        updateState {
            it.copy(lastErrorMessage = message)
        }
    }

    // --------- App Lock ---------
    fun setAppLockEnabled(enabled: Boolean) {
        updateState {
            it.copy(appLockEnabled = enabled)
        }
    }

    fun forceLock() {
        updateState {
            it.copy(isAuthenticated = false)
        }
    }

    // --------- Theme ---------
    fun setDarkTheme(enabled: Boolean) {
        updateState {
            it.copy(isDarkTheme = enabled)
        }
    }

    // --------- Security Alerts ---------
    fun setLoginAlertsEnabled(enabled: Boolean) {
        updateState {
            it.copy(loginAlertsEnabled = enabled)
        }
    }

    fun setNewDeviceAlertsEnabled(enabled: Boolean) {
        updateState {
            it.copy(newDeviceAlertsEnabled = enabled)
        }
    }

    fun setPublicWifiWarningEnabled(enabled: Boolean) {
        updateState {
            it.copy(publicWifiWarningEnabled = enabled)
        }
    }

    fun saveAccount(
        context: Context,
        name: String,
        email: String,
        password: String,
        biometricEnabled: Boolean,
        isDarkTheme: Boolean,
        appLockEnabled: Boolean,
        loginAlertsEnabled: Boolean,
        newDeviceAlertsEnabled: Boolean,
        publicWifiWarningEnabled: Boolean
    ) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .putBoolean(KEY_BIOMETRIC, biometricEnabled)
            .putBoolean(KEY_DARK_THEME, isDarkTheme)
            .putBoolean(KEY_APP_LOCK, appLockEnabled)
            .putBoolean(KEY_LOGIN_ALERTS, loginAlertsEnabled)
            .putBoolean(KEY_NEW_DEVICE_ALERTS, newDeviceAlertsEnabled)
            .putBoolean(KEY_WIFI_WARNING, publicWifiWarningEnabled)
            .apply()
    }

    fun loadAccount(context: Context): StoredAccount? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Memuat email, jika null, anggap akun belum ada dan kembalikan null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null

        val name = prefs.getString(KEY_NAME, "") ?: ""
        val password = prefs.getString(KEY_PASSWORD, "") ?: ""
        val biometricEnabled = prefs.getBoolean(KEY_BIOMETRIC, false)
        val isDarkTheme = prefs.getBoolean(KEY_DARK_THEME, false)
        val appLockEnabled = prefs.getBoolean(KEY_APP_LOCK, true)
        val loginAlertsEnabled = prefs.getBoolean(KEY_LOGIN_ALERTS, true)
        val newDeviceAlertsEnabled = prefs.getBoolean(KEY_NEW_DEVICE_ALERTS, true)
        val publicWifiWarningEnabled = prefs.getBoolean(KEY_WIFI_WARNING, true)

        return StoredAccount(
            name = name,
            email = email,
            password = password,
            biometricEnabled = biometricEnabled,
            isDarkTheme = isDarkTheme,
            appLockEnabled = appLockEnabled,
            loginAlertsEnabled = loginAlertsEnabled,
            newDeviceAlertsEnabled = newDeviceAlertsEnabled,
            publicWifiWarningEnabled = publicWifiWarningEnabled,
            deviceId = uiState.value.deviceId
        )
    }

    fun restoreAccountFromStorage(
        account: StoredAccount,
        biometricAvailable: Boolean,
        isNewDevice: Boolean
    ) {
        updateState { current ->
            current.copy(
                name = account.name,
                email = account.email,
                password = account.password,
                registeredEmail = account.email,
                registeredPassword = account.password,
                isBiometricEnabled = account.biometricEnabled && biometricAvailable,
                isDarkTheme = account.isDarkTheme,
                appLockEnabled = account.appLockEnabled,
                loginAlertsEnabled = account.loginAlertsEnabled,
                newDeviceAlertsEnabled = account.newDeviceAlertsEnabled,
                publicWifiWarningEnabled = account.publicWifiWarningEnabled,

                // ADD THIS
                isNewDevice = isNewDevice,

                isAuthenticated = true
            )
        }
    }



    fun clearAccountLocal() {
        updateState {
            AuthUIState(
                isBiometricAvailable = it.isBiometricAvailable
            )
        }
    }
}
