package com.github.se.project.model.certification

import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Section
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class EpflVerificationRepositoryTest {
  private lateinit var mockClient: OkHttpClient
  private lateinit var mockCall: Call
  private lateinit var mockResponse: Response
  private lateinit var repository: TestableEpflVerificationRepository

  @Before
  fun setup() {
    mockClient = mock()
    mockCall = mock()
    mockResponse = mock()

    repository = TestableEpflVerificationRepository(mockClient)
  }

  @Test
  fun `verifySciper with invalid SCIPER returns InvalidSciper error`() = runBlocking {
    // Test various invalid SCIPER formats
    val invalidScipers = listOf("12345", "1234567", "abcdef", "12345a")

    invalidScipers.forEach { sciper ->
      val result = repository.verifySciper(sciper)
      assertTrue(
          "Expected InvalidSciper error for $sciper",
          result is VerificationResult.Error.InvalidSciper)
    }
  }

  @Test
  fun `verifySciper with network error returns NetworkError`() = runBlocking {
    setupMockResponseWithCode(404)

    val result = repository.verifySciper("123456")
    assertTrue(result is VerificationResult.Error.NetworkError)
  }

  @Test
  fun `verifySciper with valid HTML response returns Success`() = runBlocking {
    val validHtml =
        """
            <!DOCTYPE html>
            <html>
                <head><title>EPFL People</title></head>
                <body>
                    <h1 id="name" class="pnM">John Doe</h1>
                    <button class="collapse-title-desktop">
                        Etudiant, Section de systèmes de communication - Bachelor semestre 5
                    </button>
                </body>
            </html>
        """
            .trimIndent()

    setupMockResponseWithBody(validHtml)

    val result = repository.verifySciper("123456")
    assertTrue(result is VerificationResult.Success)

    with(result as VerificationResult.Success) {
      assertEquals("John", firstName)
      assertEquals("Doe", lastName)
      assertEquals(AcademicLevel.BA5, academicLevel)
      assertEquals(Section.SC, section)
    }
  }

  @Test
  fun `verifySciper with malformed HTML returns ParsingError`() = runBlocking {
    val invalidHtml = "<html><body>Invalid format</body></html>"
    setupMockResponseWithBody(invalidHtml)

    val result = repository.verifySciper("123456")
    assertTrue(result is VerificationResult.Error.ParsingError)
  }

  @Test
  fun `verifySciper parses different academic levels correctly`() = runBlocking {
    val academicLevelTestCases =
        listOf(
            "Bachelor semestre 1" to AcademicLevel.BA1,
            "Master semestre 2" to AcademicLevel.MA2,
            "Doctorat" to AcademicLevel.PhD)

    academicLevelTestCases.forEach { (levelText, expectedLevel) ->
      val html = createHtmlWithAcademicLevel(levelText)
      setupMockResponseWithBody(html)

      val result = repository.verifySciper("123456")
      assertTrue(result is VerificationResult.Success)
      assertEquals(expectedLevel, (result as VerificationResult.Success).academicLevel)
    }
  }

  @Test
  fun `verifySciper with empty SCIPER returns InvalidSciper error`() = runBlocking {
    val result = repository.verifySciper("")
    assertTrue(result is VerificationResult.Error.InvalidSciper)
  }

  @Test
  fun `verifySciper with unexpected HTML structure returns ParsingError`() = runBlocking {
    val unexpectedHtml =
        """
            <!DOCTYPE html>
            <html>
                <head><title>EPFL People</title></head>
                <body>
                    <div>Invalid content structure</div>
                </body>
            </html>
        """
            .trimIndent()
    setupMockResponseWithBody(unexpectedHtml)

    val result = repository.verifySciper("123456")
    assertTrue(result is VerificationResult.Error.ParsingError)
  }

  @Test
  fun `verifySciper with network timeout returns NetworkError`() = runBlocking {
    whenever(mockCall.execute()).thenThrow(java.net.SocketTimeoutException("Timeout"))

    val result = repository.verifySciper("123456")
    assertTrue(result is VerificationResult.Error.NetworkError)
  }

  @Test
  fun `verifySciper with 500 server error returns NetworkError`() = runBlocking {
    setupMockResponseWithCode(500)

    val result = repository.verifySciper("123456")
    assertTrue(result is VerificationResult.Error.NetworkError)
  }

  @Test
  fun `verifySciper parses all academic levels correctly`() = runBlocking {
    val academicLevelTestCases =
        listOf(
            "Bachelor semestre 1" to AcademicLevel.BA1,
            "Bachelor semestre 2" to AcademicLevel.BA2,
            "Bachelor semestre 3" to AcademicLevel.BA3,
            "Bachelor semestre 4" to AcademicLevel.BA4,
            "Bachelor semestre 5" to AcademicLevel.BA5,
            "Bachelor semestre 6" to AcademicLevel.BA6,
            "Master semestre 1" to AcademicLevel.MA1,
            "Master semestre 2" to AcademicLevel.MA2,
            "Master semestre 3" to AcademicLevel.MA3,
            "Master semestre 4" to AcademicLevel.MA4,
            "Doctorat" to AcademicLevel.PhD,
            "Unrecognized level" to AcademicLevel.BA1 // Test the `else` case
            )

    academicLevelTestCases.forEach { (levelText, expectedLevel) ->
      val html = createHtmlWithAcademicLevel(levelText)
      setupMockResponseWithBody(html)

      val result = repository.verifySciper("123456")
      assertTrue(result is VerificationResult.Success)
      assertEquals(expectedLevel, (result as VerificationResult.Success).academicLevel)
    }
  }

  @Test
  fun `verifySciper parses all sections correctly`() = runBlocking {
    val sectionTestCases =
        listOf(
            "systèmes de communication" to Section.SC,
            "informatique" to Section.IN,
            "génie civil" to Section.GC,
            "sciences et ingénierie de l'environnement" to Section.SIE,
            "architecture" to Section.AR,
            "mathématiques" to Section.MA,
            "physique" to Section.PH,
            "génie mécanique" to Section.GM,
            "génie électrique" to Section.EL,
            "science et génie des matériaux" to Section.MX,
            "microtechnique" to Section.MT,
            "sciences et technologies du vivant" to Section.SV,
            "neuro-x" to Section.NX,
            "quantum" to Section.SIQ,
            "Unrecognized section" to Section.IN // Test the `else` case
            )

    sectionTestCases.forEach { (sectionText, expectedSection) ->
      val html = createHtmlWithSection(sectionText)
      setupMockResponseWithBody(html)

      val result = repository.verifySciper("123456")
      assertTrue(result is VerificationResult.Success)
      assertEquals(expectedSection, (result as VerificationResult.Success).section)
    }
  }

  @Test
  fun `verifySciper parses different sections correctly`() = runBlocking {
    val sectionTestCases =
        listOf(
            "systèmes de communication" to Section.SC,
            "informatique" to Section.IN,
            "mathématiques" to Section.MA)

    sectionTestCases.forEach { (sectionText, expectedSection) ->
      val html = createHtmlWithSection(sectionText)
      setupMockResponseWithBody(html)

      val result = repository.verifySciper("123456")
      assertTrue(result is VerificationResult.Success)
      assertEquals(expectedSection, (result as VerificationResult.Success).section)
    }
  }

  private fun setupMockResponseWithBody(body: String) {
    whenever(mockResponse.isSuccessful).thenReturn(true)
    whenever(mockResponse.body).thenReturn(body.toResponseBody(null))
    whenever(mockCall.execute()).thenReturn(mockResponse)
    whenever(mockClient.newCall(any())).thenReturn(mockCall)
  }

  private fun setupMockResponseWithCode(code: Int) {
    whenever(mockResponse.isSuccessful).thenReturn(false)
    whenever(mockResponse.code).thenReturn(code)
    whenever(mockCall.execute()).thenReturn(mockResponse)
    whenever(mockClient.newCall(any())).thenReturn(mockCall)
  }

  private fun createHtmlWithAcademicLevel(level: String): String =
      """
        <!DOCTYPE html>
        <html>
            <head><title>EPFL People</title></head>
            <body>
                <h1 id="name" class="pnM">John Doe</h1>
                <button class="collapse-title-desktop">
                    Etudiant, Section de informatique - $level
                </button>
            </body>
        </html>
    """
          .trimIndent()

  private fun createHtmlWithSection(section: String): String =
      """
        <!DOCTYPE html>
        <html>
            <head><title>EPFL People</title></head>
            <body>
                <h1 id="name" class="pnM">John Doe</h1>
                <button class="collapse-title-desktop">
                    Etudiant, Section de $section - Bachelor semestre 1
                </button>
            </body>
        </html>
    """
          .trimIndent()

  // Testable version of the repository that exposes protected methods for testing
  private class TestableEpflVerificationRepository(client: OkHttpClient) :
      EpflVerificationRepository(client)
}
