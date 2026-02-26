//Nia Junod & Alina Tarasevich
package pridwin.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AmbientButtonSection(
    onOpenAmbient: () -> Unit
) {
    Spacer(modifier = Modifier.height(12.dp))
    Button(onClick = onOpenAmbient) {
        Text("Ambient Controls")
    }
}