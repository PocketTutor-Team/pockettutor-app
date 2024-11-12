package com.github.se.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.se.project.model.profile.Profile

@Composable
fun DisplayTutors(
    modifier: Modifier = Modifier,
    tutors: List<Profile>,
    onCardClick: (Profile) -> Unit = {}
) {
  LazyColumn(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        itemsIndexed(tutors) { index, tutor ->
          Card(
              modifier =
                  Modifier.fillMaxWidth().testTag("tutorCard_$index").clickable {
                    onCardClick(tutor)
                  },
              colors =
                  CardDefaults.cardColors(
                      containerColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.45f)),
              shape = MaterialTheme.shapes.medium) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top) {
                      // Left side: Profile picture and info
                      Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Profile Picture
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                              Icon(
                                  imageVector = Icons.Default.Person,
                                  contentDescription = null,
                                  modifier = Modifier.padding(8.dp),
                                  tint = MaterialTheme.colorScheme.primary)
                            }

                        // Tutor Information
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                          // Name
                          Text(
                              text = "${tutor.firstName} ${tutor.lastName}",
                              style = MaterialTheme.typography.titleMedium,
                              modifier = Modifier.testTag("tutorName_$index"))

                          // Section and Level
                          Row(
                              horizontalArrangement = Arrangement.spacedBy(8.dp),
                              verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                                      Text(
                                          text = tutor.section.toString(),
                                          style = MaterialTheme.typography.labelSmall,
                                          modifier =
                                              Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                          color = MaterialTheme.colorScheme.primary)
                                    }

                                Text(
                                    text = tutor.academicLevel.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                              }

                          // Subjects
                          Text(
                              text = tutor.subjects.joinToString(", ") { it.name.lowercase() },
                              style = MaterialTheme.typography.bodySmall,
                              color = MaterialTheme.colorScheme.onSurfaceVariant,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis)
                        }
                      }

                      // Price tag
                      Surface(
                          shape = MaterialTheme.shapes.medium,
                          color = MaterialTheme.colorScheme.primary) {
                            Text(
                                text = "${tutor.price}.-/h",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onPrimary)
                          }
                    }
              }
        }
      }
}
