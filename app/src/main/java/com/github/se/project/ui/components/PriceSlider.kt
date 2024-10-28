package com.github.se.project.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun PriceSlider(sliderValue: MutableFloatState) {
  val averagePrice = 30
  Slider(
      value = sliderValue.floatValue,
      onValueChange = { sliderValue.floatValue = it },
      valueRange = 5f..50f,
      steps = 45,
      modifier = Modifier.padding(horizontal = 16.dp).testTag("priceSlider"))

  val priceDifference = averagePrice - sliderValue.floatValue.toInt()
  if (priceDifference >= 0) {
    Text(
        "Your price is ${sliderValue.floatValue.toInt()}.-, which is $priceDifference.- less than the average.",
        modifier = Modifier.testTag("priceDifferenceLow"))
  } else {
    Text(
        "Your price is ${sliderValue.floatValue.toInt()}.-, which is ${-priceDifference}.- more than the average.",
        modifier = Modifier.testTag("priceDifferenceHigh"))
  }
}
