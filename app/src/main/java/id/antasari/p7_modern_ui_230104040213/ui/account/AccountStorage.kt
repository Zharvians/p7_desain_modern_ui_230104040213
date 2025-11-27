package id.antasari.p7_modern_ui_230104040213.ui.account

import android.content.Context

data class StoredAccount(
    val name: String,
    val email: String,
    val password: String,
    val biometricEnabled: Boolean,
    val isDarkTheme: Boolean,
    val appLockEnabled: Boolean,
    val loginAlertsEnabled: Boolean,
    val newDeviceAlertsEnabled: Boolean,
    val publicWifiWarningEnabled: Boolean,
    val deviceId: String // ← WAJIB TAMBAH INI
)

object AccountStorage {
    private const val PREF_NAME = "secure_auth_prefs"

    private const val KEY_DEVICE_ID = "device_id"

    private const val KEY_NAME = "user_name"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_PASSWORD = "user_password"
    private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"

    private const val KEY_DARK_THEME = "dark_theme"
    private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
    private const val KEY_LOGIN_ALERTS = "login_alerts_enabled"
    private const val KEY_NEW_DEVICE_ALERTS = "new_device_alerts_enabled"
    private const val KEY_WIFI_WARNING = "wifi_warning_enabled"
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
        publicWifiWarningEnabled: Boolean,
        deviceId: String // ← tambah
    ) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .putBoolean(KEY_BIOMETRIC_ENABLED, biometricEnabled)
            .putBoolean(KEY_DARK_THEME, isDarkTheme)
            .putBoolean(KEY_APP_LOCK_ENABLED, appLockEnabled)
            .putBoolean(KEY_LOGIN_ALERTS, loginAlertsEnabled)
            .putString(KEY_DEVICE_ID, deviceId)
            .putBoolean(KEY_NEW_DEVICE_ALERTS, newDeviceAlertsEnabled)
            .putBoolean(KEY_WIFI_WARNING, publicWifiWarningEnabled)
            .apply()
    }

    fun loadAccount(context: Context): StoredAccount? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val deviceId = prefs.getString(KEY_DEVICE_ID, "") ?: ""
        val name = prefs.getString(KEY_NAME, "") ?: ""
        val password = prefs.getString(KEY_PASSWORD, "") ?: ""
        val biometricEnabled = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        val isDarkTheme = prefs.getBoolean(KEY_DARK_THEME, false)
        val appLockEnabled = prefs.getBoolean(KEY_APP_LOCK_ENABLED, true)
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
            deviceId = deviceId // ← tambahkan ini
        )
    }

        fun clearAccount(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}