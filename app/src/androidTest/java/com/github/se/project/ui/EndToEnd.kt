package com.github.se.project.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.MainActivity
import com.github.se.project.PocketTutorApp
import com.github.se.project.model.authentification.AuthenticationViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.profile.EditProfile
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.capture
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

    @Mock
    private var authenticationViewModel = mock(AuthenticationViewModel::class.java)

    @Mock
    lateinit var listProfilesViewModel: ListProfilesViewModel

    @Mock
    lateinit var navigationActions: NavigationActions

    @Mock
    lateinit var context: Context

    private val mockUid = "mockUid"

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        authenticationViewModel = mock(AuthenticationViewModel::class.java)
    }

    @Test
    fun skipSignIn() {
        composeTestRule.setContent { PocketTutorApp(true) }
        composeTestRule.onNodeWithTag("logo").assertIsDisplayed()
        composeTestRule.onNodeWithTag("loginButton").performClick()
        composeTestRule.onNodeWithTag("firstNameField").assertIsDisplayed()
    }
}
