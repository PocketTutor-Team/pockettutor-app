package com.github.se.project.ui.End2End

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.swipeRight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.PocketTutorApp
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.TopLevelDestinations
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
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

    @Test
    fun skipSignIn() {
        composeTestRule.setContent { PocketTutorApp(true, viewModel(), mockProfileViewModel, mockLessonViewModel) }
        //Sign In Screen
        composeTestRule.onNodeWithTag("logo").assertIsDisplayed()
        composeTestRule.onNodeWithTag("loginButton").performClick()
        composeTestRule.onNodeWithTag("firstNameField").assertIsDisplayed()

        //Create Profile Screen
        composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
        composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
        composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("0213456789")
        composeTestRule.onNodeWithTag("roleButtonTutor").performClick()
        composeTestRule.onNodeWithTag("sectionDropdown").performClick()
        composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()
        composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
        composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA3").performClick()
        composeTestRule.onNodeWithTag("confirmButton").performClick()
        assertEquals(Role.TUTOR, mockProfileViewModel.currentProfile.value?.role)

        //Create Tutor Profile Screen
        composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("checkbox_FRENCH").performClick()
        composeTestRule.onNodeWithTag("subjectButton").performClick()
        composeTestRule.onNodeWithTag("dropdownANALYSIS").performClick()
        composeTestRule.onNodeWithTag("subjectButton").performClick()
        composeTestRule.onNodeWithTag("priceSlider").performGesture { swipeRight() }
        composeTestRule.onNodeWithTag("confirmButton").performClick()

        //Create Tutor Schedule Screen
        composeTestRule
            .onNodeWithTag("welcomeText")
            .assertTextEquals("John, show us your availabilities")
        composeTestRule.onNodeWithTag("Slot_0_0").performClick()
        composeTestRule.onNodeWithTag("Slot_0_3").performClick()
        composeTestRule.onNodeWithTag("Slot_0_2").performClick()
        composeTestRule.onNodeWithTag("Slot_0_6").performClick()
        composeTestRule.onNodeWithTag("FindStudentButton").performClick()

        //Home Screen
        composeTestRule.onNodeWithContentDescription("Profile Icon").performClick()
        composeTestRule.onNodeWithTag("profileStatus").assertTextEquals("Status: BA3 Tutor")
        composeTestRule.onNodeWithTag("profileSection").assertTextEquals("Section: SC")
        composeTestRule.onNodeWithTag("profilePrice").assertTextEquals("Price: 50.- per hour")
        composeTestRule.onNodeWithTag("lessonsCount").assertTextEquals("0 lessons given since you joined PocketTutor")
        composeTestRule.onNodeWithTag("closeButton").performClick()
        composeTestRule.onNodeWithTag("Find a Student").performClick()
        composeTestRule.onNodeWithTag("Find a Student").performClick()
        composeTestRule.onNodeWithTag("My Work Space").performClick()

        Thread.sleep(5000)
    }
}
