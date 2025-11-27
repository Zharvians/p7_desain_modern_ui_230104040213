// app/src/main/java/id/antasari/p7_modern_ui_230104040213/SettingsScreen.kt
package id.antasari.p7_modern_ui_230104040213.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.antasari.p7_modern_ui_230104040213.ui.theme.P7ModernUiTheme

@Composable
fun SettingsScreen(
    userName: String,
    isBiometricEnabled: Boolean,
    appLockEnabled: Boolean,
    isDarkTheme: Boolean,
    loginAlertsEnabled: Boolean,
    newDeviceAlertsEnabled: Boolean,
    publicWifiWarningEnabled: Boolean,
    onBiometricToggle: (Boolean) -> Unit,
    onAppLockToggle: (Boolean) -> Unit,
    onDarkThemeToggle: (Boolean) -> Unit,
    onLoginAlertsToggle: (Boolean) -> Unit,
    onNewDeviceAlertsToggle: (Boolean) -> Unit,
    onPublicWifiWarningToggle: (Boolean) -> Unit,
    onDeleteAccountClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Account of $userName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Section: Security overview
            Text(
                text = "Security & privacy",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "SecureAuth status",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = buildString {
                            append("Biometric sign-in: ")
                            append(if (isBiometricEnabled) "Enabled" else "Disabled")
                            append(" | App lock: ")
                            append(if (appLockEnabled) "On" else "Off")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buildString {
                            append("Alerts: ")
                            append(if (loginAlertsEnabled) "Login" else "")
                            if (newDeviceAlertsEnabled) {
                                if (loginAlertsEnabled) append(", ")
                                append("New device")
                            }

                            if (publicWifiWarningEnabled) {
                                if (loginAlertsEnabled || newDeviceAlertsEnabled) append(", ")
                                append("Public Wi-Fi")
                            }
                            if (!loginAlertsEnabled && !newDeviceAlertsEnabled && !publicWifiWarningEnabled) {
                                append("Off")
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 16.sp
                    )
                } // Penutup Column
            } // Penutup Surface

            Spacer(modifier = Modifier.height(16.dp))

// Section: Account & sign-in
            Text(
                text = "Account & sign-in",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleItem(
                icon = Icons.Filled.Fingerprint,
                title = "Biometric sign-in",
                description = "Use fingerprint or face to unlock your SecureAuth account.",
                checked = isBiometricEnabled,
                onCheckedChange = onBiometricToggle
            )

            SettingsToggleItem(
                icon = Icons.Filled.Security,
                title = "App lock",
                description = "Lock the app automatically when you leave it for a while.",
                checked = appLockEnabled,
                onCheckedChange = onAppLockToggle
            )

            Spacer(modifier = Modifier.height(16.dp))

// Section: Alerts & notifications
            Text(
                text = "Alerts & notifications",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleItem(
                icon = Icons.Filled.Notifications,
                title = "Login alerts",
                description = "Get notified when someone signs in to your account.",
                checked = loginAlertsEnabled,
                onCheckedChange = onLoginAlertsToggle
            )
            SettingsToggleItem(
                icon = Icons.Filled.Devices,
                title = "New device alerts",
                description = "Get notified when your account is used on a new device.",
                checked = newDeviceAlertsEnabled,
                onCheckedChange = onNewDeviceAlertsToggle
            )

            SettingsToggleItem(
                icon = Icons.Filled.Wifi,
                title = "Public Wi-fi warnings",
                description = "Warn me when signing in on unsecured Wi-fi networks.",
                checked = publicWifiWarningEnabled,
                onCheckedChange = onPublicWifiWarningToggle
            )

            Spacer(modifier = Modifier.height(16.dp))

// Section: Theme
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleItem(
                icon = Icons.Filled.DarkMode,
                title = "Dark theme",
                description = "Use dark colors throughout the app.",
                checked = isDarkTheme,
                onCheckedChange = onDarkThemeToggle
            )

            Spacer(modifier = Modifier.height(20.dp))

// Danger zone
            Text(
                text = "Danger zone",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Delete account",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "This will remove your account from this device. You will need to register again.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onDeleteAccountClick,
                        modifier = Modifier.fillMaxWidth(),
                        border = ButtonDefaults.outlinedButtonBorder,
                    ) {
                        Text(
                            text = "Delete my account",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } // Penutup Column
            } // Penutup Surface
            Spacer(modifier = Modifier.height(24.dp))
        } // Penutup Column verticalScroll
    } // Penutup Surface
} // Penutup SettingsScreen @Composable

@Composable
private fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    P7ModernUiTheme {
        SettingsScreen(
            userName = "Muhayat",
            isBiometricEnabled = true,
            appLockEnabled = true,
            isDarkTheme = false,
            loginAlertsEnabled = true,
            newDeviceAlertsEnabled = true,
            publicWifiWarningEnabled = true, // <- pakai 'Wifi' (i kecil)
            onBiometricToggle = {},
            onAppLockToggle = {},
            onDarkThemeToggle = {},
            onLoginAlertsToggle = {},
            onNewDeviceAlertsToggle = {},
            onPublicWifiWarningToggle = {}, // <- pakai 'Wifi' (i kecil)
            onDeleteAccountClick = {},
            onBackClick = {}
        )
    }
}
