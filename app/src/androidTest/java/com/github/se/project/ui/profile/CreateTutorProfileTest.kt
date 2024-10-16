import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.TutoringSubject
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.profile.CreateProfileScreen
import com.github.se.project.ui.profile.CreateTutorProfile
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.util.EnumSet

@RunWith(AndroidJUnit4::class)
class CreateTutorProfileTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mocking navigation actions
    private val mockNavigationActions = Mockito.mock(NavigationActions::class.java)
    private val mockViewModel = Mockito.mock(ListProfilesViewModel::class.java).apply {
        Mockito.`when`(currentProfile).thenReturn(MutableStateFlow<Profile?>(null))
    }
    //list of languages
    private val languages = Language.entries.toTypedArray()
    private val subjects = TutoringSubject.entries.toTypedArray()

    @Test
    fun createTutorProfileScreen_rendersCorrectly() {
        (mockViewModel.currentProfile as MutableStateFlow).value =
            Profile(uid = "1", googleUid = "googleUid", firstName = "First", lastName = "Last", phoneNumber = "1234567890",
                role = Role.STUDENT, section = Section.GM, academicLevel = AcademicLevel.MA2,
                languages = EnumSet.noneOf(Language::class.java), subjects = EnumSet.noneOf(TutoringSubject::class.java), schedule = listOf())
        // Set the screen in the test environment
        composeTestRule.setContent {
            CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
        }

        composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("instructionText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tutorInfoScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("subjectText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("priceText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("priceSlider").assertIsDisplayed()
        composeTestRule.onNodeWithTag("languageSelection").assertIsDisplayed()
        composeTestRule.onNodeWithTag("subjectBox").assertIsDisplayed()
        composeTestRule.onNodeWithTag("subjectButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("languageText").assertIsDisplayed()
        languages.forEach { language ->
            composeTestRule.onNodeWithTag("${language}Text").assertIsDisplayed()
        }
        languages.forEach { language ->
            composeTestRule.onNodeWithTag("${language}Text").assertIsDisplayed()
        }
    }

    @Test
    fun formValidation_displaysToastWhenFieldsAreEmpty() {
        (mockViewModel.currentProfile as MutableStateFlow).value =
            Profile(uid = "1", googleUid = "googleUid", firstName = "First", lastName = "Last", phoneNumber = "1234567890",
                role = Role.STUDENT, section = Section.GM, academicLevel = AcademicLevel.MA2,
                languages = EnumSet.noneOf(Language::class.java), subjects = EnumSet.noneOf(TutoringSubject::class.java), schedule = listOf())
        // Set the screen in the test environment
        composeTestRule.setContent {
            CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
        }

        composeTestRule.onNodeWithTag("confirmButton").performClick()

        Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
    }

    @Test
    fun selectingLanguagesUpdatesCorrectly() {
        (mockViewModel.currentProfile as MutableStateFlow).value =
            Profile(uid = "1", googleUid = "googleUid", firstName = "First", lastName = "Last", phoneNumber = "1234567890",
                role = Role.STUDENT, section = Section.GM, academicLevel = AcademicLevel.MA2,
                languages = EnumSet.noneOf(Language::class.java), subjects = EnumSet.noneOf(TutoringSubject::class.java), schedule = listOf())
        // Set the screen in the test environment
        composeTestRule.setContent {
            CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
        }

        languages.forEach { language ->
            // Check that the language is not selected
            composeTestRule.onNodeWithTag("${language}Check").assertIsOff()

            // Select the language
            composeTestRule.onNodeWithTag("${language}Check").performClick()

            // Check that the language is now selected
            composeTestRule.onNodeWithTag("${language}Check").assertIsOn()
        }
    }

    @Test
    fun selectingSubjectsUpdatesCorrectly() {
        (mockViewModel.currentProfile as MutableStateFlow).value =
            Profile(uid = "1", googleUid = "googleUid", firstName = "First", lastName = "Last", phoneNumber = "1234567890",
                role = Role.STUDENT, section = Section.GM, academicLevel = AcademicLevel.MA2,
                languages = EnumSet.noneOf(Language::class.java), subjects = EnumSet.noneOf(TutoringSubject::class.java), schedule = listOf())
        // Set the screen in the test environment
        composeTestRule.setContent {
            CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
        }

        subjects.forEach { subject ->
            composeTestRule.onNodeWithTag("${subject.name}Text").isNotDisplayed()
            composeTestRule.onNodeWithTag("dropdown${subject.name}").isNotDisplayed()
        }
        composeTestRule.onNodeWithTag("subjectButton").performClick()

        subjects.forEach { subject ->
            //composeTestRule.onNodeWithTag("${subject}Text").assertIsDisplayed()
            composeTestRule.onNodeWithTag("dropdown${subject.name}").assertIsDisplayed()
        }

        subjects.forEach { subject ->
            // Check that the subject is not selected
            composeTestRule.onNodeWithTag("${subject.name}Checkmark").assertIsNotDisplayed()

            // Select the subject
            composeTestRule.onNodeWithTag("dropdown${subject.name}").performClick()

            // Check that the subject is now selected
            composeTestRule.onNodeWithTag("${subject.name}Checkmark", useUnmergedTree = true).assertIsDisplayed()
        }
    }
}