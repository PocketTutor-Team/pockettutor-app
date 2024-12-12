@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.se.project.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayTutors
import com.github.se.project.ui.components.ErrorState
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@Composable
fun FavoriteTutorsScreen(
    listProfilesViewModel: ListProfilesViewModel,
    navigationActions: NavigationActions,
) {
  val currentProfile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text(stringResource(R.string.no_profile_selected))

  if (currentProfile.role != Role.STUDENT) {
    ErrorState(message = stringResource(R.string.screen_only_student))
  }

  val favoriteTutors =
      currentProfile.favoriteTutors.mapNotNull { tutorId ->
        listProfilesViewModel.getProfileById(tutorId)
      }

  Scaffold(
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("topAppBar"),
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back arrow")
                  }
            },
            title = {
              Text(
                  text = stringResource(R.string.favorite_tutors),
                  style = MaterialTheme.typography.titleLarge,
                  modifier = Modifier.testTag("BookmarkedTutorsTitle"))
            })
      }) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
          if (favoriteTutors.isEmpty()) {
            Text(
                text = stringResource(R.string.no_favorite_tutors),
                modifier =
                    Modifier.align(Alignment.Center).padding(16.dp).testTag("noTutorMessage"),
                style = MaterialTheme.typography.bodyMedium)
          } else {
            DisplayTutors(
                modifier =
                    Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .testTag("tutorsList"),
                tutors = favoriteTutors,
                isFavorite = true,
                onCardClick = { tutor ->
                  listProfilesViewModel.selectProfile(tutor)
                  navigationActions.navigateTo(Screen.FAVORITE_TUTOR_DETAILS)
                })
          }
        }
      }
}
