package nsu.medpollandroid.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SecondaryText(
    text: String,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    color: Color = MaterialTheme.colors.secondary,
    shape: Shape = RectangleShape
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .background(color = color)
            .padding(horizontalPadding, 4.dp)
            .border(BorderStroke(0.dp, Color.Transparent), shape = shape),
    ) {
        Text(
            text,
            modifier = modifier
                .background(color = color),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrimaryRow(modifier: Modifier = Modifier, horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.SpaceBetween, horizontalPadding: Dp = 8.dp, topPadding: Dp = 8.dp, bottomPadding: Dp = 8.dp, color: Color = MaterialTheme.colors.primary, content: @Composable RowScope.() -> Unit) {
    FlowRow(
        modifier = modifier
            .background(color = color)
            .padding(horizontalPadding, topPadding, horizontalPadding, bottomPadding)
            .fillMaxWidth()
        ,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
fun ExpandingColumn(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    var hidden by rememberSaveable { mutableStateOf(true) }

    val newModifier = if (hidden) {
        modifier.fillMaxWidth()
    } else {
        modifier
            .fillMaxWidth()
            .padding(10.dp, 4.dp)
    }

    val color = if (hidden) {
        MaterialTheme.colors.primary} else {
        MaterialTheme.colors.secondary}
    Column(
        if (hidden) {
            Modifier
                .background(color = MaterialTheme.colors.primary)
                .padding(0.dp)
        } else {
            Modifier
                .background(color = MaterialTheme.colors.primary)
        }
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = { hidden = !hidden },
            shape = RectangleShape,
            elevation = null,
            contentPadding = PaddingValues(10.dp, 4.dp),
        ) {
            Text(
                text,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = color)
                    .padding(0.dp, 8.dp)
                    .border(BorderStroke(0.dp, Color.Transparent), shape = CircleShape),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        if (hidden) {
            return@Column
        }
        Column(modifier = newModifier) {
            content()
        }
    }
}
