package com.github.se.project.ui

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.PocketTutorApp
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

    @Mock
    lateinit var listProfilesViewModel: ListProfilesViewModel

    @Mock
    lateinit var navigationActions: NavigationActions

    @Mock
    lateinit var context: Context

    // Mock du ProfilesRepository
    private val mockProfileRepository = Mockito.mock(ProfilesRepository::class.java)

    private val mockProfileViewModel = ListProfilesViewModel(mockProfileRepository)

    private val mockLessonRepository = mock(LessonRepository::class.java)

    private val mockLessonViewModel = LessonViewModel(mockLessonRepository)

    //private var currentProfile : Profile = Profile(uid = "1", googleUid = "googleUid", firstName = "First", lastName = "Last", phoneNumber = "1234567890", role = Role.STUDENT, section = Section.GM, academicLevel = AcademicLevel.BA1, languages = listOf(), subjects = listOf(), schedule = listOf())
    var currentProfile: Profile? = null

    private val mockUid = "mockUid"

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        whenever(mockProfileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
            val onSuccess = invocation.arguments[1] as () -> Unit
            onSuccess() // Simulate a successful update
        }
        whenever(mockProfileRepository.getNewUid()).thenReturn("mockUid")
    }

    // End to end test, for the whole app, firebase included
    @Test
    fun EndToEndWithFirebase() {
        // Start the app in test mode
        composeTestRule.setContent { PocketTutorApp(true, viewModel(), mockProfileViewModel, mockLessonViewModel) }

        // Sign in
        composeTestRule.onNodeWithTag("loginButton").performClick()

        // Enter valid data for all fields
        composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
        composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
        composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("1234567890")
        composeTestRule.onNodeWithTag("roleButtonStudent").performClick()

        // Select section and academic level
        composeTestRule.onNodeWithTag("sectionDropdown").performClick()
        composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()
        composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
        composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA3").performClick()

        // Click the confirm button
        composeTestRule.onNodeWithTag("confirmButton").performClick()

        // Go to the profile viewing screen
        composeTestRule.onNodeWithTag("profileIcon", true).performClick()

        // Check if the profile is displayed
        composeTestRule.onNodeWithTag("profileView").assertIsDisplayed()
    }
}
