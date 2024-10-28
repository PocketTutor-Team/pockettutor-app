package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.AvailabilityGrid
import com.github.se.project.ui.navigation.NavigationActions

@Composable
fun EditTutorSchedule(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory)
) {
    val profile =
        listProfilesViewModel.currentProfile.collectAsState().value
            ?: return Text(text = "No Profile selected. Should not happen.", color = Color.Red)

    var currentSchedule by remember { mutableStateOf(profile.schedule) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            IconButton(onClick = { navigationActions.goBack() },
                modifier = Modifier.testTag("editScheduleCloseButton")) { Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go Back") }
        },
        content = { paddingValues ->
            Column(
                modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .padding(paddingValues)
                    .testTag("editAvailabilityScreen")) {
                Text(
                    text = "${profile.firstName}, show us your availabilities",
                    modifier = Modifier,//.padding(vertical = 0.dp).testTag("welcomeText"),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Start)
                Text(
                    "Modify the time slots you're available during the week:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("editInstructionsText"))

                Spacer(modifier = Modifier.height(8.dp))

                AvailabilityGrid(
                    schedule = currentSchedule,
                    onScheduleChange = { updatedSchedule -> currentSchedule = updatedSchedule },
                    modifier = Modifier.weight(1f))
            }
        },
        bottomBar = {
            Button(
                modifier = Modifier.fillMaxWidth().padding(14.dp).testTag("editScheduleUpdateButton"),
                shape = MaterialTheme.shapes.medium,
                onClick = {
                    profile.schedule = currentSchedule
                    listProfilesViewModel.updateProfile(profile)
                    navigationActions.goBack()
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                }) {
                Text(text = "Update Schedule", fontSize = 16.sp)
            }
        })
}
