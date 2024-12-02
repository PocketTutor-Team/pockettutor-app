package com.github.se.project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.se.project.utils.Country
import com.github.se.project.utils.countries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberInput(
    selectedCountry: Country,
    onCountryChange: (Country) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit
) {
  var countryCodeExpanded by remember { mutableStateOf(false) }

  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    // Country code dropdown
    ExposedDropdownMenuBox(
        expanded = countryCodeExpanded,
        onExpandedChange = { countryCodeExpanded = !countryCodeExpanded },
        modifier = Modifier.weight(1f)) {
          OutlinedTextField(
              readOnly = true,
              value = "${selectedCountry.flagEmoji} ${selectedCountry.code}",
              onValueChange = {},
              label = { Text("Country Code") },
              trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryCodeExpanded)
              },
              modifier =
                  Modifier.menuAnchor()
                      .fillMaxWidth()
                      .testTag("countryCodeField") // Add test tag here
              )
          ExposedDropdownMenu(
              modifier = Modifier.fillMaxWidth(),
              expanded = countryCodeExpanded,
              onDismissRequest = { countryCodeExpanded = false }) {
                countries.forEach { country ->
                  DropdownMenuItem(
                      text = {
                        Text(text = "${country.flagEmoji} ${country.name} (${country.code})")
                      },
                      onClick = {
                        onCountryChange(country)
                        countryCodeExpanded = false
                      },
                      modifier =
                          Modifier.testTag(
                              "country_${country.code.replace("+", "plus")}") // Add test tag here
                      )
                }
              }
        }

    Spacer(modifier = Modifier.width(8.dp))

    // Phone number input
    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChange,
        label = { Text("Phone Number") },
        modifier = Modifier.weight(2f).testTag("phoneNumberField"), // Ensure test tag is set
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
  }
}

fun isPhoneNumberValid(countryCode: String, phoneNumber: String): Boolean {
  val fullPhoneNumber = countryCode + phoneNumber
  val phoneNumberRegex = "^\\+?[0-9]{10,15}\$".toRegex()
  return fullPhoneNumber.matches(phoneNumberRegex)
}
