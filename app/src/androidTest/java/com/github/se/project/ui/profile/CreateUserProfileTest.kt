package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class CreateProfileScreenTest {
    private lateinit var profilesRepository: ProfilesRepository
    private lateinit var navigationActions: NavigationActions
    private lateinit var listProfilesViewModel: ListProfilesViewModel

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Mock is a way to create a fake object that can be used in place of a real object
        profilesRepository = mock(ProfilesRepository::class.java)
        navigationActions = mock(NavigationActions::class.java)
        listProfilesViewModel = ListProfilesViewModel(profilesRepository)

        // Mock the current route to be the create profile screen
        `when`(navigationActions.currentRoute()).thenReturn(Screen.CREATE_PROFILE)
    }

    @Test
    fun displayAllComponents() {
        composeTestRule.setContent {
            CreateProfileScreen(navigationActions, listProfilesViewModel, googleUid = "test_google_uid")
        }

        // Check that all necessary components are displayed
        composeTestRule.onNodeWithTag("createProfileButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("firstNameInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("lastNameInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("phoneNumberInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("roleSelection").assertIsDisplayed()
        composeTestRule.onNodeWithTag("sectionInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("academicLevelInput").assertIsDisplayed()
    }

    @Test
    fun testProfileTutorCreationValidInput() {
        composeTestRule.setContent {
            CreateProfileScreen(navigationActions, listProfilesViewModel, googleUid = "test_google_uid")
        }

        // Input valid first name, last name, phone number, section, and academic level
        composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
        composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
        composeTestRule.onNodeWithTag("phoneNumberInput").performTextInput("0123456789")
        composeTestRule.onNodeWithTag("roleButton_TUTOR").performClick()
        composeTestRule.onNodeWithTag("sectionInput").performTextInput("GM")
        composeTestRule.onNodeWithTag("academicLevelInput").performTextInput("MA2")

        // Click the create profile button
        composeTestRule.onNodeWithTag("createProfileButton").performClick()

        // Verify that the navigation action was called to the HOME screen
        //verify(navigationActions).navigateTo(Screen.HOME)
    }

    @Test
    fun testProfileStudentCreationValidInput() {
        composeTestRule.setContent {
            CreateProfileScreen(navigationActions, listProfilesViewModel, googleUid = "test_google_uid")
        }

        // Input valid first name, last name, phone number, section, and academic level
        composeTestRule.onNodeWithTag("firstNameInput").performTextInput("John")
        composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
        composeTestRule.onNodeWithTag("phoneNumberInput").performTextInput("0123456789")
        composeTestRule.onNodeWithTag("roleButton_STUDENT").performClick()
        composeTestRule.onNodeWithTag("sectionInput").performTextInput("GM")
        composeTestRule.onNodeWithTag("academicLevelInput").performTextInput("MA2")

        // Click the create profile button
        composeTestRule.onNodeWithTag("createProfileButton").performClick()

        // Verify that the navigation action was called to the HOME screen
        //verify(navigationActions).navigateTo(Screen.HOME)
    }

    @Test
    fun testProfileCreationInvalidPhoneNumber() {
        composeTestRule.setContent {
            CreateProfileScreen(navigationActions, listProfilesViewModel, googleUid = "test_google_uid")
        }

        // Input valid first name, last name, and invalid phone number
        composeTestRule.onNodeWithTag("firstNameInput").performTextInput("Jane")
        composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
        composeTestRule.onNodeWithTag("phoneNumberInput").performTextInput("123ABC")
        composeTestRule.onNodeWithTag("sectionInput").performTextInput("GM")
        composeTestRule.onNodeWithTag("academicLevelInput").performTextInput("BA5")

        // Click the create profile button
        composeTestRule.onNodeWithTag("createProfileButton").performClick()

        composeTestRule.onNodeWithTag("errorText").assertTextEquals("Please enter a valid phone number!")

        // Verify that the navigation action was not called
        verify(navigationActions, org.mockito.Mockito.never()).navigateTo(Screen.HOME)
    }

    @Test
    fun testProfileCreationWrongAcademicLevel() {
        composeTestRule.setContent {
            CreateProfileScreen(navigationActions, listProfilesViewModel, googleUid = "test_google_uid")
        }

        // Input first name only, leave other fields empty
        composeTestRule.onNodeWithTag("firstNameInput").performTextInput("Alice")
        composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
        composeTestRule.onNodeWithTag("phoneNumberInput").performTextInput("0123456789")
        composeTestRule.onNodeWithTag("roleButton_STUDENT").performClick()
        composeTestRule.onNodeWithTag("sectionInput").performTextInput("GX")
        composeTestRule.onNodeWithTag("academicLevelInput").performTextInput("BA5")
        // Click the create profile button
        composeTestRule.onNodeWithTag("createProfileButton").performClick()
        composeTestRule.onNodeWithTag("errorText").assertTextEquals("Please select a section and an academic level from the dropdown menu!")

        // Verify that the navigation action was not called
        verify(navigationActions, org.mockito.Mockito.never()).navigateTo(Screen.HOME)
    }

    @Test
    fun testProfileCreationIncompleteInput() {
        composeTestRule.setContent {
            CreateProfileScreen(navigationActions, listProfilesViewModel, googleUid = "test_google_uid")
        }

        // Input first name only, leave other fields empty
        composeTestRule.onNodeWithTag("firstNameInput").performTextInput("Alice")
        composeTestRule.onNodeWithTag("lastNameInput").performTextInput("Doe")
        composeTestRule.onNodeWithTag("phoneNumberInput").performTextInput("0123456789")
        // Click the create profile button
        composeTestRule.onNodeWithTag("createProfileButton").performClick()
        composeTestRule.onNodeWithTag("errorText").assertTextEquals("Please complete all the fields before creating your account!")

        // Verify that the navigation action was not called
        verify(navigationActions, org.mockito.Mockito.never()).navigateTo(Screen.HOME)
    }

}
