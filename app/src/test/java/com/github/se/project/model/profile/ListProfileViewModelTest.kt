package com.github.se.project.model.profile

import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq

class ListToDosViewModelTest {
  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var listProfilesViewModel: ListProfilesViewModel

  val profile =
      Profile(
          uid = "1",
          token = "",
          googleUid = "1",
          firstName = "Pocket",
          lastName = "Tutor",
          phoneNumber = "0213456789",
          role = Role.TUTOR,
          section = Section.GM,
          academicLevel = AcademicLevel.MA2,
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ALGEBRA),
          schedule =
              List(7) { day ->
                List(12) {
                  if (day == 0) 1 else 0 // Set 1 for the first row and 0 for the others
                }
              },
          price = 50)

  @Before
  fun setUp() {
    profilesRepository = mock(ProfilesRepository::class.java)
    listProfilesViewModel = ListProfilesViewModel(profilesRepository)
  }

  @Test
  fun getNewUid() {
    `when`(profilesRepository.getNewUid()).thenReturn("uid")
    assertThat(listProfilesViewModel.getNewUid(), `is`("uid"))
  }

  @Test
  fun getProfilesCallsRepository() {
    listProfilesViewModel.getProfiles()
    verify(profilesRepository).getProfiles(any(), any())
  }

  @Test
  fun addToDoCallsRepository() {
    listProfilesViewModel.addProfile(profile)
    verify(profilesRepository).addProfile(eq(profile), any(), any())
  }

  @Test
  fun updateProfileCallsRepository() {
    listProfilesViewModel.updateProfile(profile)
    verify(profilesRepository).updateProfile(eq(profile), any(), any())
  }

  @Test
  fun deleteProfileCallsRepository() {
    listProfilesViewModel.deleteProfileById(profile.uid)
    verify(profilesRepository).deleteProfileById(eq(profile.uid), any(), any())
  }

  @Test
  fun getProfilesCallsOnSuccess() = runBlocking {
    val profiles = listOf(profile)
    val onSuccessCaptor = argumentCaptor<(List<Profile>) -> Unit>()

    // Mock repository to call the success callback
    listProfilesViewModel.getProfiles()
    verify(profilesRepository).getProfiles(onSuccessCaptor.capture(), any())
    onSuccessCaptor.firstValue.invoke(profiles)

    // Collect the profiles from the StateFlow
    val collectedProfiles = listProfilesViewModel.profiles.value

    // Verify that the ViewModel handled the result as expected
    assertThat(collectedProfiles, `is`(profiles))
  }
}
