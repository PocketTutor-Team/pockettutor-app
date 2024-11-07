package com.github.se.project.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.project.model.profile.Language

@Composable
fun LanguageSelector(selectedLanguages: MutableList<Language>) {
  val languages = Language.entries.toTypedArray()

  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.fillMaxWidth()
              .padding(8.dp)
              .testTag("languageSelectorRow") // Tag for the entire row
      ) {
        languages.forEachIndexed { index, language ->
          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier =
                  Modifier.weight(1f)
                      .padding(end = 8.dp)
                      .testTag("languageRow_$index") // Tag for each language row
              ) {
                Checkbox(
                    checked = selectedLanguages.contains(language),
                    onCheckedChange = { isSelected ->
                      if (!selectedLanguages.contains(language)) {
                        selectedLanguages.add(language)
                      } else {
                        selectedLanguages.remove(language)
                      }
                    },
                    modifier =
                        Modifier.size(24.dp)
                            .testTag("checkbox_${language.name}") // Tag for each checkbox
                    )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = language.name.lowercase(),
                    style = MaterialTheme.typography.titleSmall,
                    modifier =
                        Modifier.align(Alignment.CenterVertically)
                            .testTag("languageText_${language.name}") // Tag for each language text
                    )
              }
        }
      }
}
