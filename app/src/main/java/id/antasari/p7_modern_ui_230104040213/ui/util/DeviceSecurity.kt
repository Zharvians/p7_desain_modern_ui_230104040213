// app/src/main/java/id/antasari/p7_modern_ui_230104040213/util/DeviceSecurity.kt
package id.antasari.p7_modern_ui_230104040213.ui.util

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object DeviceSecurity {

    // Simple root checks
    fun isDeviceRooted(): Boolean {
        return checkBuildTags() || checkSuperUserApk() || checkForSuBinary()
    }

    private fun checkBuildTags(): Boolean {
        val tags = Build.TAGS
        return !tags.isNullOrEmpty() && tags.contains("test-keys")
    }

    private fun checkSuperUserApk(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun checkForSuBinary(): Boolean {
        try {
            val p = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(p.inputStream))
            val result = `in`.readLine()
            return !result.isNullOrEmpty()
        } catch (_: Exception) { }
        return false
    }

    // Very simple VPN detection (presence of vpn in active networks)
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isVpnActive(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }

    // Basic "public WiFi" heuristic: open network without internet or SSID contains common public names
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isLikelyPublicWifi(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false

        // if not wifi, return false
        if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return false

        // fallback: check SSID via WifiManager (requires ACCESS_FINE_LOCATION or ACCESS_WIFI_STATE)
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = wifiManager.connectionInfo
            val ssid = info?.ssid?.trim('"')
            if (!ssid.isNullOrEmpty()) {
                val publicHints = listOf("free", "guest", "public", "cafe", "mall", "hotel", "airport")
                if (publicHints.any { ssid.lowercase().contains(it) }) return true
            }
        } catch (_: Exception) { }

        // If captive portal / no validated internet (quick heuristic omitted for brevity)
        return false
    }
}
