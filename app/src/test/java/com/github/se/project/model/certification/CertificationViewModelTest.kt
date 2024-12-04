package com.github.se.project.model.certification

import com.github.se.project.model.profile.*
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class CertificationViewModelTest {
  private lateinit var repository: EpflVerificationRepository
  private lateinit var profilesViewModel: ListProfilesViewModel
  private lateinit var viewModel: CertificationViewModel
  private lateinit var testDispatcher: TestDispatcher
  private lateinit var mockProfile: Profile

  @Before
  fun setup() {
    testDispatcher = StandardTestDispatcher()
    Dispatchers.setMain(testDispatcher)

    repository = mock()
    profilesViewModel = mock()
    mockProfile = createMockProfile()

    whenever(profilesViewModel.currentProfile).thenReturn(MutableStateFlow(mockProfile))

    viewModel = CertificationViewModel(repository, profilesViewModel)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `resetVerification resets state and attempts`() = runTest {
    // Setup mock to always return network error
    whenever(repository.verifySciper(any())).thenReturn(VerificationResult.Error.NetworkError)

    // Use up all attempts
    repeat(3) {
      viewModel.verifySciperNumber("123456")
      testDispatcher.scheduler.advanceUntilIdle()
    }

    // Try one more time to confirm we're blocked
    viewModel.verifySciperNumber("123456")
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify we're actually in TooManyAttempts state
    val stateBeforeReset = viewModel.verificationState.value
    assertTrue(
        "Should be in TooManyAttempts state before reset",
        stateBeforeReset is CertificationViewModel.VerificationState.TooManyAttempts)

    // Reset verification
    viewModel.resetVerification()

    // Configure repository to return success for the next attempt
    val successResult =
        VerificationResult.Success(
            firstName = "John",
            lastName = "Doe",
            academicLevel = AcademicLevel.BA1,
            section = Section.IN)
    whenever(repository.verifySciper(any())).thenReturn(successResult)

    // Try verification again
    viewModel.verifySciperNumber("123456")
    testDispatcher.scheduler.advanceUntilIdle()

    // Should be successful now
    val stateAfterReset = viewModel.verificationState.value
    assertTrue(
        "Should be in Success state after reset",
        stateAfterReset is CertificationViewModel.VerificationState.Success)

    // Verify the repository was called the correct number of times
    verify(repository, times(4)).verifySciper(any())
  }

  @Test
  fun `verifySciperNumber with valid SCIPER updates state to Success`() = runTest {
    val successResult =
        VerificationResult.Success(
            firstName = "John",
            lastName = "Doe",
            academicLevel = AcademicLevel.BA1,
            section = Section.IN)

    whenever(repository.verifySciper("123456")).thenReturn(successResult)

    viewModel.verifySciperNumber("123456")
    testDispatcher.scheduler.advanceUntilIdle()

    assertTrue(
        viewModel.verificationState.value is CertificationViewModel.VerificationState.Success)
    assertEquals(
        successResult,
        (viewModel.verificationState.value as CertificationViewModel.VerificationState.Success)
            .result)
  }

  @Test
  fun `verifySciperNumber with invalid SCIPER updates state to Error`() = runTest {
    whenever(repository.verifySciper("12345")).thenReturn(VerificationResult.Error.InvalidSciper)

    viewModel.verifySciperNumber("12345")
    testDispatcher.scheduler.advanceUntilIdle()

    assertTrue(viewModel.verificationState.value is CertificationViewModel.VerificationState.Error)
    assertEquals(
        VerificationResult.Error.InvalidSciper,
        (viewModel.verificationState.value as CertificationViewModel.VerificationState.Error).error)
  }

  @Test
  fun `verifySciperNumber with network error updates state to Error`() = runTest {
    whenever(repository.verifySciper("123456")).thenReturn(VerificationResult.Error.NetworkError)

    viewModel.verifySciperNumber("123456")
    testDispatcher.scheduler.advanceUntilIdle()

    assertTrue(viewModel.verificationState.value is CertificationViewModel.VerificationState.Error)
    assertEquals(
        VerificationResult.Error.NetworkError,
        (viewModel.verificationState.value as CertificationViewModel.VerificationState.Error).error)
  }

  @Test
  fun `verifySciperNumber updates profile after successful verification`() = runTest {
    val successResult =
        VerificationResult.Success(
            firstName = "John",
            lastName = "Doe",
            academicLevel = AcademicLevel.BA1,
            section = Section.IN)

    whenever(repository.verifySciper("123456")).thenReturn(successResult)

    viewModel.verifySciperNumber("123456")
    testDispatcher.scheduler.advanceUntilIdle()

    verify(profilesViewModel)
        .updateProfile(
            argThat { profile ->
              profile.firstName == "John" &&
                  profile.lastName == "Doe" &&
                  profile.academicLevel == AcademicLevel.BA1 &&
                  profile.section == Section.IN &&
                  profile.certification?.verified == true &&
                  profile.certification?.sciper == "123456"
            })
  }

  @Test
  fun `verifySciperNumber blocks after max attempts reached`() = runTest {
    repeat(4) {
      viewModel.verifySciperNumber("123456")
      testDispatcher.scheduler.advanceUntilIdle()
    }

    assertTrue(
        viewModel.verificationState.value
            is CertificationViewModel.VerificationState.TooManyAttempts)
    verify(repository, times(3)).verifySciper(any()) // Should only call 3 times, not 4
  }

  @Test
  fun `isProfileVerified returns correct verification status`() {
    // Test unverified profile
    whenever(profilesViewModel.currentProfile)
        .thenReturn(
            MutableStateFlow(
                mockProfile.copy(certification = EpflCertification("123456", verified = false))))
    assertFalse(viewModel.isProfileVerified())

    // Test verified profile
    whenever(profilesViewModel.currentProfile)
        .thenReturn(
            MutableStateFlow(
                mockProfile.copy(certification = EpflCertification("123456", verified = true))))
    assertTrue(viewModel.isProfileVerified())

    // Test null certification
    whenever(profilesViewModel.currentProfile)
        .thenReturn(MutableStateFlow(mockProfile.copy(certification = null)))
    assertFalse(viewModel.isProfileVerified())
  }

  @Test
  fun `initial state should be Idle`() {
    assertTrue(
        "Initial state should be Idle",
        viewModel.verificationState.value is CertificationViewModel.VerificationState.Idle)
  }

  @Test
  fun `verifySciperNumber with parsing error updates state to Error`() = runTest {
    whenever(repository.verifySciper("123456")).thenReturn(VerificationResult.Error.ParsingError)

    viewModel.verifySciperNumber("123456")
    testDispatcher.scheduler.advanceUntilIdle()

    assertTrue(viewModel.verificationState.value is CertificationViewModel.VerificationState.Error)
    assertEquals(
        VerificationResult.Error.ParsingError,
        (viewModel.verificationState.value as CertificationViewModel.VerificationState.Error).error)
  }

  @Test
  fun `resetVerification should work without reaching max attempts`() = runTest {
    whenever(repository.verifySciper("123456")).thenReturn(VerificationResult.Error.InvalidSciper)

    // Trigger verification once
    viewModel.verifySciperNumber("123456")
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify state is Error
    assertTrue(
        "State should be Error",
        viewModel.verificationState.value is CertificationViewModel.VerificationState.Error)

    // Reset verification
    viewModel.resetVerification()
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify state is Idle
    assertTrue(
        "State should be reset to Idle",
        viewModel.verificationState.value is CertificationViewModel.VerificationState.Idle)
  }

  @Test
  fun `profile state does not update on verification error`() = runTest {
    whenever(repository.verifySciper("12345")).thenReturn(VerificationResult.Error.InvalidSciper)

    viewModel.verifySciperNumber("12345")
    testDispatcher.scheduler.advanceUntilIdle()

    verify(profilesViewModel, never()).updateProfile(any())
    assertTrue(
        "State should remain Error",
        viewModel.verificationState.value is CertificationViewModel.VerificationState.Error)
  }

  @Test
  fun `handle unexpected repository exception`() = runTest {
    whenever(repository.verifySciper(any())).thenThrow(RuntimeException("Unexpected error"))

    viewModel.verifySciperNumber("123456")
    testDispatcher.scheduler.advanceUntilIdle()

    assertTrue(
        "State should be Error",
        viewModel.verificationState.value is CertificationViewModel.VerificationState.Error)
    val error =
        (viewModel.verificationState.value as CertificationViewModel.VerificationState.Error).error
    assertTrue(error is VerificationResult.Error.NetworkError) // Default to NetworkError
  }

  @Test
  fun `verify state transitions`() = runTest {
    // Transition: Idle -> Error
    whenever(repository.verifySciper("123456")).thenReturn(VerificationResult.Error.InvalidSciper)
    viewModel.verifySciperNumber("123456")
    testDispatcher.scheduler.advanceUntilIdle()
    assertTrue(viewModel.verificationState.value is CertificationViewModel.VerificationState.Error)

    // Transition: Error -> Idle (reset)
    viewModel.resetVerification()
    testDispatcher.scheduler.advanceUntilIdle()
    assertTrue(viewModel.verificationState.value is CertificationViewModel.VerificationState.Idle)

    // Transition: Idle -> Success
    val successResult =
        VerificationResult.Success(
            firstName = "John",
            lastName = "Doe",
            academicLevel = AcademicLevel.BA1,
            section = Section.IN)
    whenever(repository.verifySciper("123456")).thenReturn(successResult)
    viewModel.verifySciperNumber("123456")
    testDispatcher.scheduler.advanceUntilIdle()
    assertTrue(
        viewModel.verificationState.value is CertificationViewModel.VerificationState.Success)
  }

  private fun createMockProfile() =
      Profile(
          uid = "test-uid",
          googleUid = "google-uid",
          firstName = "Original",
          lastName = "Name",
          phoneNumber = "1234567890",
          role = Role.STUDENT,
          section = Section.IN,
          academicLevel = AcademicLevel.BA1)
}
