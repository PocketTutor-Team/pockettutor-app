package com.android.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//Composable funtion to display the tutor sign up info screen
@Preview(showBackground = true)
@Composable
fun TutorInfo(){
    val analyseChecked = remember { mutableStateOf(false) }
    val algebreChecked = remember { mutableStateOf(false) }
    val physiqueChecked = remember { mutableStateOf(false) }
    val frenchChecked = remember { mutableStateOf(false) }
    val englishChecked = remember { mutableStateOf(false) }
    val germanChecked = remember { mutableStateOf(false) }

    Column { //Column composable to display the tutor sign up info
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("Welcome Tutor!", fontSize= 24.sp)
        }
        Text("Complete your profile by electing your capabilities:", Modifier.padding(start=4.dp))

        Text("What languages do you feel comfortable teaching in?", Modifier.padding(start= 16.dp, top = 32.dp))
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

        Text("Which subjects do you teach?", Modifier.padding(start= 16.dp, 4.dp))
        Row(modifier = Modifier.padding(2.dp)) {
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
        }

        //Button confirming the tutor sign up info
        Button(
            onClick = { /*TODO: Add navigation and save info to firebase*/ },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
        ) {
            Text("Confirm")
        }
    }
}