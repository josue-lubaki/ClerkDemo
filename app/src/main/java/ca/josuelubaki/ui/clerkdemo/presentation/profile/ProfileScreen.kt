package ca.josuelubaki.ui.clerkdemo.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clerk.api.Clerk
import com.clerk.ui.auth.AuthView
import com.clerk.ui.userbutton.UserButton
import com.clerk.ui.userprofile.UserProfileView

@Composable
fun ProfileScreen() {
    val user by Clerk.userFlow.collectAsStateWithLifecycle()
    if (user != null) {
        UserProfileView()
    }

//    if (user == null) {
//        UserButton()
//    } else {
//        AuthView()
//    }
}