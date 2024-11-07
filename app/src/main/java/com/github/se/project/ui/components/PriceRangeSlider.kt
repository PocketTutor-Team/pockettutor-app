package com.github.se.project.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun PriceRangeSlider(
    label: String,
    onValueChange: (Float, Float) -> Unit,
    initialStart: Float = 5f, // Default value for initialStart
    initialEnd: Float = 50f,
) {
  var sliderPosition by remember { mutableStateOf(initialStart..initialEnd) }

  Column(modifier = Modifier.testTag("priceRangeSliderColumn")) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.testTag("priceRangeSliderLabel"))

    RangeSlider(
        value = sliderPosition,
        steps = 44,
        onValueChange = { range ->
          sliderPosition = range
          onValueChange(sliderPosition.start, sliderPosition.endInclusive)
        },
        valueRange = 5f..50f,
        colors =
            SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondaryContainer,
                activeTrackColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.testTag("priceRangeSlider"))
  }
}
