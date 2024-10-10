package com.android.sample

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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.TutoringSubject

//Composable funtion to display the tutor sign up info screen
@Composable
fun TutorInfo(profile:Profile){
    val analyseChecked = remember { mutableStateOf(false) }
    val algebreChecked = remember { mutableStateOf(false) }
    val physiqueChecked = remember { mutableStateOf(false) }
    val frenchChecked = remember { mutableStateOf(false) }
    val englishChecked = remember { mutableStateOf(false) }
    val germanChecked = remember { mutableStateOf(false) }
    val average = 30

    val expanded = remember { mutableStateOf(false) }
    val sliderValue = remember { mutableFloatStateOf(5f) }

    val profilee = remember {mutableStateOf(profile)}

    Column { //Column composable to display the tutor sign up info
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("Welcome Tutor!", fontSize = 24.sp)
        }
        Text("Complete your profile by electing your capabilities:", Modifier.padding(start = 4.dp))

        Text(
            "What languages do you feel comfortable teaching in?",
            Modifier.padding(start = 16.dp, top = 32.dp)
        )
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

        Text("Which subjects do you teach?", Modifier.padding(start = 16.dp, 4.dp))

        Box {
            Button(onClick = { expanded.value = true }) {
                Text("Select Subjects")
            }
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Row {
                            if (analyseChecked.value) {
                                Icon(Icons.Filled.Check, contentDescription = null)
                            }
                            Text("Analyse")
                        }
                    },
                    onClick = { analyseChecked.value = !analyseChecked.value }
                )
                DropdownMenuItem(
                    text = {
                        Row {
                            if (algebreChecked.value) {
                                Icon(Icons.Filled.Check, contentDescription = null)
                            }
                            Text("Algèbre")
                        }
                    },
                    onClick = { algebreChecked.value = !algebreChecked.value }
                )
                DropdownMenuItem(
                    text = {
                        Row {
                            if (physiqueChecked.value) {
                                Icon(Icons.Filled.Check, contentDescription = null)
                            }
                            Text("Physique")
                        }
                    },
                    onClick = { physiqueChecked.value = !physiqueChecked.value }
                )
            }
        }

        /*Row(modifier = Modifier.padding(2.dp)) {
            Checkbox(
                checked = analyseChecked.value,
                onCheckedChange = { analyseChecked.value = it },
            )
            Text("Analyse", modifier = Modifier.padding(top = 14.dp))
            Checkbox(
                checked = algebreChecked.value,
                onCheckedChange = { algebreChecked.value = it },
            )
            Text("Algèbre", modifier = Modifier.padding(top = 14.dp))
            Checkbox(
                checked = physiqueChecked.value,
                onCheckedChange = { physiqueChecked.value = it },
            )
            Text("Physique", modifier = Modifier.padding(top = 14.dp))
        }*/

        // Slider price selection
        Text(
            "Select your tutoring price for an hour:",
            Modifier.padding(start = 16.dp, top = 16.dp)
        )
        Slider(
            value = sliderValue.floatValue,
            onValueChange = { sliderValue.floatValue = it },
            valueRange = 5f..50f,
            steps = 45, // Ensures slider snaps to integers
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        val priceDifference = average - sliderValue.floatValue.toInt()
        if (priceDifference >= 0)
            Text(
                "Your price is currently ${sliderValue.floatValue.toInt()}.-. This is $priceDifference.- less than the average price on PocketTutor.",
                Modifier.padding(start = 16.dp, top = 8.dp)
            )
        else
            Text(
                "Your price is currently ${sliderValue.floatValue.toInt()}.-. This is ${-priceDifference}.- more than the average price on PocketTutor.",
                Modifier.padding(start = 16.dp, top = 8.dp)
            )

        //Button confirming the tutor sign up info
        Button(
            onClick = {
                profile.languages.remove(Language.STUDENT)
                if (frenchChecked.value) {
                    profilee.value.languages.add(Language.FRENCH)
                }
                if (englishChecked.value) {
                    profilee.value.languages.add(Language.ENGLISH)
                }
                if (germanChecked.value) {
                    profile.languages.add(Language.GERMAN)
                }
                if (analyseChecked.value) {
                    profilee.value.subjects.add(TutoringSubject.ANALYSIS)
                }
                if (algebreChecked.value) {
                    profile.subjects.add(TutoringSubject.ALGEBRA)
                }
                if (physiqueChecked.value) {
                    profile.subjects.add(TutoringSubject.PHYSICS)
                }
                profilee.value.price = sliderValue.floatValue.toInt()
                /*TODO: Add navigation and save info to firebase*/
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
        ) {
            Text("Confirm")
        }
        Text(frenchChecked.toString())
        Text(profilee.toString())
    }
}