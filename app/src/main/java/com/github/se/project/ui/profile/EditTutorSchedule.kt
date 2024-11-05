package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
            ?: return Text(
                text = "No Profile selected. Should not happen.",
                color = Color.Red,
                modifier = Modifier.testTag("editScheduleNoProfile"))

    var profileSchedule by remember { mutableStateOf(profile.schedule) }
    LaunchedEffect(profile.schedule) { profileSchedule = profile.schedule }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            IconButton(
                onClick = { navigationActions.goBack() },
                modifier = Modifier.testTag("editScheduleCloseButton")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Go Back")
            }
        },
        content = { paddingValues ->
            LazyColumn(
                modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .padding(paddingValues)
                    .testTag("editAvailabilityScreen"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "${profile.firstName}, show us your availabilities",
                        modifier = Modifier.testTag("editScheduleWelcomeText"),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Start
                    )
                }
                item {
                    Text(
                        "Modify the time slots you're available during the week:",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.testTag("editScheduleInstructionsText")
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    AvailabilityGrid(
                        schedule = profileSchedule,
                        onScheduleChange = { updatedSchedule -> profileSchedule = updatedSchedule }
                    )
                }
            }
        },
        bottomBar = {
            Button(
                shape = MaterialTheme.shapes.medium,
                onClick = {
                    profile.schedule = profileSchedule
                    listProfilesViewModel.updateProfile(profile)
                    navigationActions.goBack()
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(14.dp).testTag("editScheduleButton")) {
                Text(text = "Update Schedule", fontSize = 16.sp)
            }
        }
    )
}

