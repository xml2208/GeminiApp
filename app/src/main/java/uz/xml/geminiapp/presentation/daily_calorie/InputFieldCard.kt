package uz.xml.geminiapp.presentation.daily_calorie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.xml.geminiapp.presentation.theme.CardBackground
import uz.xml.geminiapp.presentation.theme.FieldBackground
import uz.xml.geminiapp.presentation.theme.HighlightColor
import uz.xml.geminiapp.presentation.theme.PurpleAccent

@Composable
fun InputFieldCard(
    label: String,
    highlight: Boolean = false,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (highlight) HighlightColor else PurpleAccent)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (highlight) HighlightColor else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = FieldBackground,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp)
            ) {
                content()
            }
        }
    }
}
