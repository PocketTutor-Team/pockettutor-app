package com.github.se.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.profile.Profile

@Composable
fun DisplayTutors(
    modifier: Modifier = Modifier,
    tutors: List<Profile>,
    onCardClick: (Profile) -> Unit = {}
) {
  LazyColumn(
      modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(tutors) { index, tutor ->
          Card(
              modifier =
                  Modifier.fillMaxWidth().testTag("tutorCard_$index").clickable {
                    onCardClick(tutor)
                  },
              colors =
                  CardDefaults.cardColors(
                      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
              shape = RoundedCornerShape(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                      // Left side: Profile picture and price
                      Column(
                          horizontalAlignment = Alignment.CenterHorizontally,
                          verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Profile Picture
                          Box(contentAlignment = Alignment.Center) {
                              Surface(
                                  modifier = Modifier.size(48.dp),
                                  shape = CircleShape,
                                  color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.padding(8.dp),
                                        tint = MaterialTheme.colorScheme.secondary)
                                  }

                              if (tutor.certification?.verified == true) {
                                  Surface(
                                      modifier =
                                      Modifier.size(24.dp)
                                          .align(Alignment.BottomEnd)
                                          .offset(x = 8.dp, y = 8.dp),
                                      shape = CircleShape,
                                      color = MaterialTheme.colorScheme.background,
                                      shadowElevation = 2.dp) {
                                      Surface(
                                          modifier = Modifier.padding(2.dp).fillMaxSize(),
                                          shape = CircleShape,
                                          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                                          Icon(
                                              imageVector = ImageVector.vectorResource(id = R.drawable.epflpng),
                                              contentDescription = "EPFL Verified",
                                              modifier = Modifier.padding(2.dp),
                                              tint = Color.Red)
                                      }
                                  }
                              }
                          }

                          Spacer(modifier = Modifier.size(2.dp))

                          // Price tag
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.secondary) {
                                  Text(
                                      text = "${tutor.price}.-/h",
                                      style = MaterialTheme.typography.titleSmall,
                                      modifier =
                                          Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                      color = Color(0xFFFFFFFF))
                                }
                          }

                      // Tutor Information
                      Column(
                          verticalArrangement = Arrangement.spacedBy(4.dp),
                          modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${tutor.firstName} ${tutor.lastName}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.testTag("tutorName_$index"))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                  Surface(
                                      shape = RoundedCornerShape(8.dp),
                                      color =
                                          MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)) {
                                        Text(
                                            text = tutor.section.toString(),
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier =
                                                Modifier.padding(
                                                    horizontal = 12.dp, vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.secondary)
                                      }

                                  Text(
                                      text = tutor.academicLevel.toString(),
                                      style = MaterialTheme.typography.bodyMedium,
                                      color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                            Text(
                                text = tutor.subjects.joinToString(", ") { it.name.lowercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                overflow = TextOverflow.Ellipsis)
                          }
                    }
              }
        }
      }
}
