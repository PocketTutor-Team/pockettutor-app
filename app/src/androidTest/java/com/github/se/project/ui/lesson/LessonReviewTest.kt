package com.github.se.project.ui.lesson

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRating
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.components.LessonReviewDialog
import org.junit.Rule
import org.junit.Test

class LessonReviewDialogTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val mockLesson =
      Lesson(
          id = "test-id",
          title = "Math Lesson",
          description = "Test lesson description",
          subject = Subject.ANALYSIS,
          languages = listOf(),
          tutorUid = listOf("tutor-id"),
          studentUid = "student-id",
          minPrice = 20.0,
          maxPrice = 30.0,
          price = 25.0,
          timeSlot = "01/01/2024T14:00:00",
          status = LessonStatus.COMPLETED,
          latitude = 0.0,
          longitude = 0.0)

  private val mockTutor =
      Profile(
          uid = "tutor-id",
          token = "",
          googleUid = "google-tutor-id",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.MA1)

  private val mockInitialRating = LessonRating(grade = 4, comment = "Great lesson!")

  @Test
  fun dialog_DisplaysCorrectInitialContent() {
    // When
    composeTestRule.setContent {
      LessonReviewDialog(
          lesson = mockLesson,
          tutor = mockTutor,
          initialRating = null,
          onDismiss = {},
          onSubmitReview = { _, _ -> })
    }

    // Then
    composeTestRule.onNodeWithText("Review Your Lesson").assertExists()
    composeTestRule.onNodeWithText(mockLesson.title).assertExists()
    composeTestRule.onNodeWithText(mockLesson.subject.name).assertExists()
    composeTestRule
        .onNodeWithText("with ${mockTutor.firstName} ${mockTutor.lastName}")
        .assertExists()
    composeTestRule.onNodeWithText("Rate your experience").assertExists()
  }

  @Test
  fun dialog_ShowsInitialRatingCorrectly() {
    // When
    composeTestRule.setContent {
      LessonReviewDialog(
          lesson = mockLesson,
          tutor = mockTutor,
          initialRating = mockInitialRating,
          onDismiss = {},
          onSubmitReview = { _, _ -> })
    }

    // Then
    composeTestRule.onNode(hasText(mockInitialRating.comment)).assertExists()
  }

  @Test
  fun dialog_SubmitButtonInitiallyDisabled() {
    // When
    composeTestRule.setContent {
      LessonReviewDialog(
          lesson = mockLesson,
          tutor = mockTutor,
          initialRating = null,
          onDismiss = {},
          onSubmitReview = { _, _ -> })
    }

    // Then
    composeTestRule.onNodeWithText("Submit").assertIsNotEnabled()
  }

  @Test
  fun dialog_SubmitButtonEnabledAfterRating() {
    // When
    composeTestRule.setContent {
      LessonReviewDialog(
          lesson = mockLesson,
          tutor = mockTutor,
          initialRating = null,
          onDismiss = {},
          onSubmitReview = { _, _ -> })
    }

    // Click on the third star
    composeTestRule.onAllNodes(hasContentDescription("Star 3")).onFirst().performClick()

    // Then
    composeTestRule.onNodeWithText("Submit").assertIsEnabled()
  }

  @Test
  fun dialog_SubmitCallbackTriggered() {
    var submittedRating = 0
    var submittedComment = ""

    // When
    composeTestRule.setContent {
      LessonReviewDialog(
          lesson = mockLesson,
          tutor = mockTutor,
          initialRating = null,
          onDismiss = {},
          onSubmitReview = { rating, comment ->
            submittedRating = rating
            submittedComment = comment
          })
    }

    // Select rating and enter comment
    composeTestRule.onAllNodes(hasContentDescription("Star 4")).onFirst().performClick()
    composeTestRule.onNode(hasText("Your feedback")).performTextInput("Great tutor!")
    composeTestRule.onNodeWithText("Submit").performClick()

    // Then
    assert(submittedRating == 4)
    assert(submittedComment == "Great tutor!")
  }

  @Test
  fun dialog_DismissCallbackTriggered() {
    var dismissCalled = false

    // When
    composeTestRule.setContent {
      LessonReviewDialog(
          lesson = mockLesson,
          tutor = mockTutor,
          initialRating = null,
          onDismiss = { dismissCalled = true },
          onSubmitReview = { _, _ -> })
    }

    // Click dismiss button
    composeTestRule.onNodeWithText("Later").performClick()

    // Then
    assert(dismissCalled)
  }

  @Test
  fun dialog_TextFieldHandlesInput() {
    // When
    composeTestRule.setContent {
      LessonReviewDialog(
          lesson = mockLesson,
          tutor = mockTutor,
          initialRating = null,
          onDismiss = {},
          onSubmitReview = { _, _ -> })
    }

    // Enter text in feedback field
    val testComment = "This is a test comment"
    composeTestRule.onNode(hasText("Your feedback")).performTextInput(testComment)

    // Then
    composeTestRule.onNode(hasText(testComment)).assertExists()
  }

  @Test
  fun dialog_HandlesNoTutorProvided() {
    // When
    composeTestRule.setContent {
      LessonReviewDialog(
          lesson = mockLesson,
          tutor = null,
          initialRating = null,
          onDismiss = {},
          onSubmitReview = { _, _ -> })
    }

    // Then
    composeTestRule
        .onNodeWithText("with ${mockTutor.firstName} ${mockTutor.lastName}")
        .assertDoesNotExist()
  }
}
