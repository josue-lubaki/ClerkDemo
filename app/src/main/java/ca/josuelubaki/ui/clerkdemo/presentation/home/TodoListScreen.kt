package ca.josuelubaki.ui.clerkdemo.presentation.home

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josuelubaki.ui.clerkdemo.BuildConfig
import com.clerk.api.Clerk
import com.clerk.api.network.serialization.successOrNull
import com.clerk.api.session.GetTokenOptions
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY,
) {
    install(Postgrest)

    // Le JWT Clerk est injecté automatiquement avant chaque requête Supabase
    accessToken = {
        Clerk.auth.getToken(GetTokenOptions(template = "supabase")).successOrNull()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(onCreateClick: () -> Unit) {
    var items by remember { mutableStateOf<List<TodoItem>>(listOf()) }

    LaunchedEffect(Unit) {
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from("todos").select().decodeList<TodoItem>()
            }
        }.onSuccess { result ->
            Log.d("TodoListScreen", "✅ ${result.size} todo(s) chargé(s)")
            items = result
        }.onFailure { error ->
            Log.e("TodoListScreen", "Erreur chargement todos", error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Todos") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create item"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(
                items,
                key = { item -> item.id },
            ) { item ->
                Text(
                    item.name,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
    }
}

@Serializable
data class TodoItem(val id: Int, val name: String)
