package com.github.se.project.model.certification

import android.util.Log
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Section
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

/**
 * Repository responsible for verifying EPFL profiles through SCIPER numbers. Handles network
 * requests to the EPFL website and parses returned HTML to extract user information.
 *
 * @property client OkHttpClient instance for making network requests
 */
open class EpflVerificationRepository(private val client: OkHttpClient = OkHttpClient()) {
  companion object {
    private const val EPFL_URL = "https://people.epfl.ch/"
    private const val TAG = "EpflVerificationRepo"
  }

  /**
   * Attempts to verify a SCIPER number by fetching and parsing the corresponding EPFL profile.
   *
   * @param sciper The 6-digit SCIPER number to verify
   * @return VerificationResult indicating success with profile data or specific error
   */
  suspend fun verifySciper(sciper: String): VerificationResult =
      withContext(Dispatchers.IO) {
        if (!isValidSciper(sciper)) {
          Log.w(TAG, "Invalid SCIPER format: $sciper")
          return@withContext VerificationResult.Error.InvalidSciper
        }

        return@withContext try {
          // Build and execute the HTTP request
          val response = client.newCall(Request.Builder().url(EPFL_URL + sciper).build()).execute()

          if (!response.isSuccessful) {
            Log.e(TAG, "HTTP error ${response.code} for SCIPER: $sciper")
            return@withContext VerificationResult.Error.NetworkError
          }

          val html =
              response.body?.string()
                  ?: run {
                    Log.e(TAG, "Empty response body for SCIPER: $sciper")
                    return@withContext VerificationResult.Error.NetworkError
                  }

          // Parse the HTML and extract profile information
          parseEpflProfile(html)
        } catch (e: Exception) {
          Log.e(TAG, "Unexpected error while verifying SCIPER: $sciper", e)
          VerificationResult.Error.NetworkError
        }
      }

  /**
   * Validates the format of a SCIPER number.
   *
   * @param sciper The SCIPER number to validate
   * @return true if the SCIPER is exactly 6 digits, false otherwise
   */
  private fun isValidSciper(sciper: String): Boolean {
    return sciper.matches(Regex("^\\d{6}$"))
  }

  /**
   * Parses the EPFL profile HTML page to extract user information.
   *
   * @param html The HTML content of the EPFL profile page
   * @return VerificationResult containing extracted profile data or parsing error
   */
  private fun parseEpflProfile(html: String): VerificationResult {
    try {
      val document = Jsoup.parse(html)

      // Get name from h1 tag with class "pnM"
      val nameElement =
          document.select("h1#name.pnM").firstOrNull()
              ?: return VerificationResult.Error.ParsingError

      val fullName = nameElement.text().trim()
      val nameParts = fullName.split(" ").filter { it.isNotBlank() }

      // Extract first and last name
      // Take first part as firstName and last part as lastName
      if (nameParts.size < 2) {
        Log.e(TAG, "Could not parse name properly from: $fullName")
        return VerificationResult.Error.ParsingError
      }

      val firstName = nameParts.first()
      val lastName = nameParts.last()

      // Find the academic information in the collapse-title-desktop button
      val academicInfo =
          document.select("button.collapse-title-desktop").firstOrNull()
              ?: return VerificationResult.Error.ParsingError

      // Extract section and level information from the text
      // Example text: "Etudiant, Section de systèmes de communication - Bachelor semestre 5"
      val infoText = academicInfo.text()

      // Parse academic level
      val academicLevel =
          when {
            infoText.contains("Bachelor semestre 1") -> AcademicLevel.BA1
            infoText.contains("Bachelor semestre 2") -> AcademicLevel.BA2
            infoText.contains("Bachelor semestre 3") -> AcademicLevel.BA3
            infoText.contains("Bachelor semestre 4") -> AcademicLevel.BA4
            infoText.contains("Bachelor semestre 5") -> AcademicLevel.BA5
            infoText.contains("Bachelor semestre 6") -> AcademicLevel.BA6
            infoText.contains("Master semestre 1") -> AcademicLevel.MA1
            infoText.contains("Master semestre 2") -> AcademicLevel.MA2
            infoText.contains("Master semestre 3") -> AcademicLevel.MA3
            infoText.contains("Master semestre 4") -> AcademicLevel.MA4
            infoText.contains("Doctorat") || infoText.contains("PhD") -> AcademicLevel.PhD
            else -> AcademicLevel.BA1
          }

      // Parse section
      val section =
          when {
            infoText.contains("systèmes de communication") -> Section.SC
            infoText.contains("informatique") -> Section.IN
            infoText.contains("génie civil") -> Section.GC
            infoText.contains("sciences et ingénierie de l'environnement") -> Section.SIE
            infoText.contains("architecture") -> Section.AR
            infoText.contains("mathématiques") -> Section.MA
            infoText.contains("physique") -> Section.PH
            infoText.contains("génie mécanique") -> Section.GM
            infoText.contains("génie électrique") -> Section.EL
            infoText.contains("science et génie des matériaux") -> Section.MX
            infoText.contains("microtechnique") -> Section.MT
            infoText.contains("sciences et technologies du vivant") -> Section.SV
            infoText.contains("neuro-x") -> Section.NX
            infoText.contains("quantum") -> Section.SIQ
            else -> Section.IN
          }

      return VerificationResult.Success(
          firstName = firstName,
          lastName = lastName,
          academicLevel = academicLevel,
          section = section)
    } catch (e: Exception) {
      Log.e(TAG, "Error parsing EPFL profile HTML", e)
      return VerificationResult.Error.ParsingError
    }
  }
}
