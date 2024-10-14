package com.github.se.project.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.TutoringSubject
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

// Composable function to display the tutor sign up info screen
@Composable
fun TutorInfoScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory)
) {
  val profile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text(text = "No Profile selected. Should not happen.", color = Color.Red)

  val analysisChecked = remember { mutableStateOf(false) }
  val algebraChecked = remember { mutableStateOf(false) }
  val physicsChecked = remember { mutableStateOf(false) }
  val frenchChecked = remember { mutableStateOf(false) }
  val englishChecked = remember { mutableStateOf(false) }
  val germanChecked = remember { mutableStateOf(false) }
  val average = 30

  val expanded = remember { mutableStateOf(false) }
  val sliderValue = remember { mutableFloatStateOf(5f) }
  val languageflag = remember { mutableStateOf(false) }
  val subjectflag = remember { mutableStateOf(false) }

  Column { // Column composable to display the tutor sign up info
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
      Text("Welcome Tutor!", fontSize = 24.sp)
    }
    Text("Complete your profile by electing your capabilities:", Modifier.padding(start = 4.dp))

    // Language selection
    Text(
        "What languages do you feel comfortable teaching in?",
        Modifier.padding(start = 16.dp, top = 32.dp))
    Row(modifier = Modifier.padding(2.dp)) {
      Checkbox(
          checked = frenchChecked.value,
          onCheckedChange = { frenchChecked.value = it },
      )
      Text("French", modifier = Modifier.padding(top = 14.dp))
      Checkbox(
          checked = englishChecked.value,
          onCheckedChange = { englishChecked.value = it },
      )
      Text("English", modifier = Modifier.padding(top = 14.dp))
      Checkbox(
          checked = germanChecked.value,
          onCheckedChange = { germanChecked.value = it },
      )
      Text("German", modifier = Modifier.padding(top = 14.dp))
    }

    // Subject selection
    Text("Which subjects do you teach?", Modifier.padding(start = 16.dp, 4.dp))

    Box {
      Button(onClick = { expanded.value = true }, Modifier.padding(start = 16.dp)) {
        Text("Select Subjects")
      }
      DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
        DropdownMenuItem(
            text = {
              Row {
                if (analysisChecked.value) {
                  Icon(Icons.Filled.Check, contentDescription = null)
                }
                Text("Analysis")
              }
            },
            onClick = { analysisChecked.value = !analysisChecked.value })
        DropdownMenuItem(
            text = {
              Row {
                if (algebraChecked.value) {
                  Icon(Icons.Filled.Check, contentDescription = null)
                }
                Text("Algebra")
              }
            },
            onClick = { algebraChecked.value = !algebraChecked.value })
        DropdownMenuItem(
            text = {
              Row {
                if (physicsChecked.value) {
                  Icon(Icons.Filled.Check, contentDescription = null)
                }
                Text("Physics")
              }
            },
            onClick = { physicsChecked.value = !physicsChecked.value })
      }
    }

    // Slider price selection
    Text("Select your tutoring price for an hour:", Modifier.padding(start = 16.dp, top = 16.dp))
    Slider(
        value = sliderValue.floatValue,
        onValueChange = { sliderValue.floatValue = it },
        valueRange = 5f..50f, // Price range from 5.- to 50.-
        steps = 45, // Ensures slider snaps to integers
        modifier = Modifier.padding(horizontal = 16.dp))
    val priceDifference = average - sliderValue.floatValue.toInt()
    if (priceDifference >= 0)
        Text(
            "Your price is currently ${sliderValue.floatValue.toInt()}.-. This is $priceDifference.- less than the average price on PocketTutor.",
            Modifier.padding(start = 16.dp, top = 8.dp))
    else
        Text(
            "Your price is currently ${sliderValue.floatValue.toInt()}.-. This is ${-priceDifference}.- more than the average price on PocketTutor.",
            Modifier.padding(start = 16.dp, top = 8.dp))

    // Tutor sign up info confirmation
    Button(
        onClick = {
          if (!frenchChecked.value &&
              !englishChecked.value &&
              !germanChecked.value) { // Check if at least one language is selected
            languageflag.value = true
            subjectflag.value = false
          } else if (!analysisChecked.value &&
              !algebraChecked.value &&
              !physicsChecked.value) { // Check if at least one subject is selected
            languageflag.value = false
            subjectflag.value = true
          } else { // If all info is correct, save the info to the profile
            languageflag.value = false
            subjectflag.value = false
            if (frenchChecked.value) {
              profile.languages.add(Language.FRENCH)
            }
            if (englishChecked.value) {
              profile.languages.add(Language.ENGLISH)
            }
            if (germanChecked.value) {
              profile.languages.add(Language.GERMAN)
            }
            if (analysisChecked.value) {
              profile.subjects.add(TutoringSubject.ANALYSIS)
            }
            if (algebraChecked.value) {
              profile.subjects.add(TutoringSubject.ALGEBRA)
            }
            if (physicsChecked.value) {
              profile.subjects.add(TutoringSubject.PHYSICS)
            }
            profile.price = sliderValue.floatValue.toInt()

            // Update the profile in the database with the new information
            listProfilesViewModel.updateProfile(profile)

            // Navigate to the next screen (ie to enter availability information)
            navigationActions.navigateTo(Screen.CREATE_CALENDAR)
          }
        },
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)) {
          Text("Confirm")
        }
    if (languageflag.value) { // Display error message if no language is selected
      Text(
          "Please select at least one language", Modifier.padding(start = 16.dp), color = Color.Red)
    }
    if (subjectflag.value) { // Display error message if no subject is selected
      Text("Please select at least one subject", Modifier.padding(start = 16.dp), color = Color.Red)
    }
  }
}
