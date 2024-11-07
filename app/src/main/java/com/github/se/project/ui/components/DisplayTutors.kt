package com.github.se.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.project.model.profile.Profile

@Composable
fun DisplayTutors(
    modifier: Modifier = Modifier,
    tutors: List<Profile>,
    onCardClick: (Profile) -> Unit = {}
) {
  LazyColumn(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
    itemsIndexed(tutors) { index, tutor ->
      Card(
          modifier =
              Modifier.fillMaxWidth()
                  .padding(vertical = 2.dp)
                  .testTag("tutorCard_$index")
                  .clickable { onCardClick(tutor) },
          colors =
              CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth().testTag("tutorContent_$index"),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                      text = tutor.firstName + tutor.lastName,
                      style = MaterialTheme.typography.titleMedium,
                      modifier = Modifier.testTag("tutorName_$index"))

                  Text(
                      text = tutor.section.toString() + tutor.academicLevel.toString(),
                      style = MaterialTheme.typography.bodyLarge,
                      modifier = Modifier.testTag("tutorLevel_$index"))
                }
          }
    }
  }
}
