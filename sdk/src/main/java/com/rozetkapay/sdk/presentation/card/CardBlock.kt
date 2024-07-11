package com.rozetkapay.sdk.presentation.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CardBlock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BasicText(
                modifier = Modifier.padding(vertical = 4.dp),
                text = "Card number"
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BasicText(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    text = "Exp. date"
                )
                BasicText(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    text = "Cvv. date"
                )
            }
        }
    }
}

@Composable
@Preview
private fun CardBlockPreview() {
    CardBlock()
}
