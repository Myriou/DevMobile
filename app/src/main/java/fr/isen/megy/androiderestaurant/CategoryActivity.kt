package fr.isen.megy.androiderestaurant
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.megy.androiderestaurant.ui.theme.AndroidERestaurantTheme

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // Récupérer le nom de la catégorie passé en argument
                val category = intent.getStringExtra("category") ?: ""

                // Utiliser la fonction composable pour créer la barre d'applications
                CategoryScreen(category)
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(category: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(category)
                }
            )
        }
    ) {
        innerPadding ->
        ScrollContent(innerPadding,category)
        Text(category)

    }
}


@Composable
fun ScrollContent(innerPadding: PaddingValues , category: String) {

    val itemsList = when (category) {
        "Entrées" -> stringArrayResource(R.array.entrees)
        "Plats" -> stringArrayResource(R.array.plats)
        "Desserts" -> stringArrayResource(R.array.desserts)
        else -> emptyArray()
    }

    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        items(itemsList) { item ->
            Text(item)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTopAppBar(category: String) {
    TopAppBar(title = { Text(category) })
}