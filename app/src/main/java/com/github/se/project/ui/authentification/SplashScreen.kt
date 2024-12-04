import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.se.project.R
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SplashScreen(
    navController: NavController,
    listProfilesViewModel: ListProfilesViewModel,
) {
  val user = FirebaseAuth.getInstance().currentUser
  val profiles by listProfilesViewModel.profiles.collectAsState(initial = emptyList())

  // Navigation logic
  LaunchedEffect(user, profiles) {
    if (user != null) {
      if (profiles.isNotEmpty()) {
        val profile = profiles.find { it.googleUid == user.uid }
        if (profile != null) {
          // set the google uid of the user
          listProfilesViewModel.setCurrentProfile(profile.copy(googleUid = user.uid))
          navController.navigate(Screen.HOME) { popUpTo(Screen.SPLASH) { inclusive = true } }
        } else {
          navController.navigate(Screen.CREATE_PROFILE) {
            popUpTo(Screen.SPLASH) { inclusive = true }
          }
        }
      }
    } else {
      navController.navigate(Screen.AUTH) { popUpTo(Screen.SPLASH) { inclusive = true } }
    }
  }

  // Animation for the logo
  val infiniteTransition = rememberInfiniteTransition(label = "blink")
  val scale by
      infiniteTransition.animateFloat(
          initialValue = 0.8f,
          targetValue = 1.1f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(1000, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse),
          label = "bliiink")

  // UI Content
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      // Animated Logo
      Image(
          painter = painterResource(id = R.drawable.logobrand),
          contentDescription = "Pocket Tutor Logo",
          modifier = Modifier.size(150.dp).scale(scale))
      Spacer(modifier = Modifier.height(16.dp))
      // Circular Loading Indicator
      CircularProgressIndicator(
          color = MaterialTheme.colors.primary, strokeWidth = 4.dp, modifier = Modifier.size(50.dp))
    }
  }
}
