package com.github.se.project.model.authentification

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.project.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.security.MessageDigest
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/** ViewModel class for managing authentication actions and user sign-in */
class AuthenticationViewModel : ViewModel() {

  // MutableLiveData to hold the current user ID, initialized to null
  val userId = MutableLiveData<String?>(null)

  /**
   * Function to handle Google Sign-In with a provided context and callback for success.
   *
   * @param context The context used to initialize the CredentialManager and perform the Google
   *   sign-in.
   * @param onSuccess Lambda function that will be triggered when sign-in is successful, providing
   *   the user ID.
   */
  fun handleGoogleSignIn(context: Context, onSuccess: (uid: String) -> Unit) {
    viewModelScope.launch {
      // Start Google sign-in and collect the result asynchronously
      googleSignIn(context).collect { result ->
        result.fold( // Fold function allows handling success and failure cases
            onSuccess = { authResult -> // If sign-in is successful
              val currentUser = authResult.user
              if (currentUser != null) {
                userId.value = currentUser.uid // Set the user ID in LiveData

                // Show success message
                Toast.makeText(context, "Sign in successful", Toast.LENGTH_LONG).show()

                // Trigger the onSuccess callback with the user ID
                onSuccess(userId.value!!)
              }
            },
            onFailure = { e -> // If there is an error in sign-in
              // Show error message with the exception message
              Toast.makeText(context, "Authentication error: ${e.message}", Toast.LENGTH_LONG)
                  .show()
            })
      }
    }
  }

  /**
   * Function to perform Google Sign-In and return a Flow with the AuthResult.
   *
   * @param context The context used for credential manager and Google Sign-In configuration.
   * @return A Flow of Result<AuthResult> that represents the sign-in result.
   */
  private suspend fun googleSignIn(context: Context): Flow<Result<AuthResult>> {
    // Initialize Firebase authentication instance
    val firebaseAuth = FirebaseAuth.getInstance()

    // Return a Flow that will emit the result of the Google Sign-In process
    return callbackFlow {
      try {
        // Initialize the Credential Manager to handle credential retrieval
        val credentialManager: CredentialManager = CredentialManager.create(context)

        // Generate a nonce (random value) for security purposes
        val ranNonce: String = UUID.randomUUID().toString()
        val bytes: ByteArray = ranNonce.toByteArray()
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        val digest: ByteArray = md.digest(bytes)
        val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }

        // Set up Google ID option with necessary parameters for authentication
        val googleIdOption: GetGoogleIdOption =
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Allow selection of any account
                .setServerClientId(context.getString(R.string.web_client_id)) // Specify client ID
                .setNonce(hashedNonce) // Prevent replay attacks with a unique nonce
                .setAutoSelectEnabled(true) // Automatically select account if only one exists
                .build()

        // Create a credential request with the Google ID option
        val request: GetCredentialRequest =
            GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        // Retrieve the credentials from the Credential Manager
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential

        // Check if the credential is a valid Google ID Token
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          // Extract the Google ID Token credential
          val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

          // Create an AuthCredential from the Google ID Token
          val authCredential =
              GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

          // Sign in with Firebase using the AuthCredential
          val authResult = firebaseAuth.signInWithCredential(authCredential).await()

          // Emit the result as successful sign-in
          trySend(Result.success(authResult))
        } else {
          // If credential is invalid, log the error
          Log.e("AuthenticationViewModel", "Invalid credential type received.")
          throw RuntimeException("Received an invalid credential type.")
        }
      } catch (e: GetCredentialCancellationException) {
        // Handle case where sign-in is canceled by the user
        trySend(Result.failure(Exception("Sign-in was canceled.")))
      } catch (e: Exception) {
        // Log and emit error for any other exceptions
        Log.e("AuthenticationViewModel", "Error during authentication process", e)
        trySend(Result.failure(e))
      }

      // Ensure proper closure of the callbackFlow
      awaitClose {}
    }
  }

  /**
   * Function to handle Firebase sign-out and notify when done.
   *
   * @param onSignOutComplete Lambda function that will be triggered when the sign-out process is
   *   complete.
   */
  fun signOut(onSignOutComplete: () -> Unit) {
    // Initialize Firebase Auth instance
    val firebaseAuth = FirebaseAuth.getInstance()

    try {
      // Perform sign-out from Firebase
      firebaseAuth.signOut()

      // Clear the user ID from LiveData as the user has signed out
      userId.value = null

      // Notify the caller that sign-out is complete
      onSignOutComplete()
    } catch (e: Exception) {
      // Log and handle any errors during sign-out
      Log.e("AuthenticationViewModel", "Error during sign-out", e)
    }
  }
}
