// app/src/main/java/id/antasari/p7_modern_ui_230104040213/ui/theme/Shape.kt
package id.antasari.p7_modern_ui_230104040213.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp), // chip, badge
    medium = RoundedCornerShape(16.dp), // card kecil, text field
    large = RoundedCornerShape(24.dp), // button utama, card besar
)
