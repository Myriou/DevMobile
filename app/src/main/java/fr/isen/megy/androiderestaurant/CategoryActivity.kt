package fr.isen.megy.androiderestaurant
import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.compose.material3.TextButton
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
import com.android.volley.Response
import fr.isen.megy.androiderestaurant.ui.theme.AndroidERestaurantTheme
import org.json.JSONObject
class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // Récupérer le nom de la catégorie passé en argument
                val category = intent.getStringExtra("category") ?: ""

                // Utiliser la fonction composable pour créer la barre d'applications
                CategoryScreen(category) { dishName ->
                    navigateToDetailActivity(dishName)
                }
            }
        }
    }
    private fun navigateToDetailActivity(dishName: String) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("dishName", dishName)
        }
        startActivity(intent)
    }
    fun startActivity(){
        val intent = Intent(this, DetailActivity::class.java)
        startActivity(intent)
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(category: String, onCategoryClick: (String) -> Unit) {
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
        ScrollContent(innerPadding,category, onCategoryClick )


    }
}


@Composable
fun ScrollContent(innerPadding: PaddingValues, category: String, onCategoryClick: (String) -> Unit) {
    val itemsList = when (category) {
        "Entrées" -> stringArrayResource(R.array.entrees)
        "Plats" -> stringArrayResource(R.array.plats)
        "Desserts" -> stringArrayResource(R.array.desserts)
        else -> emptyArray()
    }

    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        items(itemsList) { dishName ->
            TextButton(
                text = dishName,
                onClick = { onCategoryClick(dishName) } // Appel de la fonction onCategoryClick fournie par l'activité
            )
        }
    }
}


@Composable
fun TextButton(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick, // Utilisez simplement la fonction onClick fournie
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = text, style = TextStyle(color = Color.Black))
    }
}