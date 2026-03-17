package ca.josuelubaki.ui.clerkdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josuelubaki.ui.clerkdemo.presentation.MainUiState
import ca.josuelubaki.ui.clerkdemo.presentation.MainViewModel
import ca.josuelubaki.ui.clerkdemo.presentation.profile.ProfileScreen
import ca.josuelubaki.ui.clerkdemo.presentation.sign.SignInOrUpScreen
import ca.josuelubaki.ui.clerkdemo.presentation.sign.signin.SignInUiState
import ca.josuelubaki.ui.clerkdemo.ui.theme.ClerkDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClerkDemoTheme {
                val viewModel: MainViewModel by viewModels()
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when (state) {
                        MainUiState.Loading -> CircularProgressIndicator()
                        MainUiState.SignedOut -> SignInOrUpScreen()
//                        MainUiState.SignedIn -> Button(onClick = { viewModel.signOut() }) { Text("Sign out") }
                        MainUiState.SignedIn -> ProfileScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ClerkDemoTheme {
        Greeting("Android")
    }
}