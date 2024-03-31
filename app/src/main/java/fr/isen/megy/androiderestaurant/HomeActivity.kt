package fr.isen.megy.androiderestaurant


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.megy.androiderestaurant.ui.theme.AndroidERestaurantTheme
import kotlinx.coroutines.delay

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                AutoUpdate(this)
                // A surface container using the 'background' color from the theme
                SmallTopAppBarExample(this) { category ->
                    if (category == "Plats" || category == "Entrées" || category == "Desserts") {
                        // Ouvrir CategoryActivity lorsque "Plats" est cliqué

                        navigateToCategory(category)
                    } else {
                        // Afficher le toast pour les autres catégories
                        //showToast(category)
                        startActivity()
                    }
                }
            }
        }
    }
    fun showToast (text:String){
        Toast.makeText(
            this,
            text,
            Toast.LENGTH_SHORT
        ).show()

    }
    private fun navigateToCategory(category: String) {
        val intent = Intent(this, CategoryActivity::class.java).apply {
            // Ajouter le nom de la catégorie en tant qu'extra dans l'intent
            putExtra("category", category)
        }
        startActivity(intent)
    }
    fun startActivity(){
        val intent = Intent(this, CategoryActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample(context : Context,onCategoryClick: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Garfield's")
                },
                actions = {
                    CartIconWithBadge(
                        cartItemCount = getCartItemCount(context),
                        onItemClick = {
                            // Rediriger l'utilisateur vers l'écran du panier
                            context.startActivity(Intent(context, CartActivity::class.java))
                        }
                    )
                }
            )
        }
    )  { innerPadding ->
        ScrollContent(innerPadding, onCategoryClick)
    }
}

@Composable
fun ScrollContent(innerPadding: PaddingValues, onCategoryClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Partie supérieure avec l'image et le texte "Bienvenue chez ChatRestaurant"
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(), // Occupe toute la largeur de l'écran
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)

                ) {
                    Text(
                        text = "Bienvenue",
                        style = TextStyle(color = Color.DarkGray, fontSize = 24.sp), // Augmente la taille du texte
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "chez",
                        style = TextStyle(color = Color.DarkGray, fontSize = 20.sp), // Augmente la taille du texte
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Garfield's",
                        style = TextStyle(color = Color.DarkGray, fontSize = 30.sp), // Augmente la taille du texte
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.chat),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()

                )
            }
        }
        // Partie inférieure avec les boutons "Entrées", "Plats" et "Desserts"
        Column(
            modifier = Modifier
                .fillMaxWidth() // Occupe toute la largeur de l'écran
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val categories = listOf("Entrées", "Plats", "Desserts")

            categories.forEach { category ->
                TextButtonExample(
                    text = category,
                    onClick = { onCategoryClick(category) } // Appel de la fonction onCategoryClick fournie par l'activité
                )
            }
        }
    }
}

@Composable
fun TextButtonExample(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick, // Utilisez simplement la fonction onClick fournie
        modifier = Modifier.padding(vertical = 8.dp)

    ) {
        Text(text = text, style = TextStyle(color = Color.Black),fontSize = 40.sp)

    }
}


@Composable
fun AutoUpdate(context: Context) {

            val cartItems = loadCartItemsFromJson(context)
            val itemCount = cartItems.sumBy { it.quantity }
            updateCartItemCount(context, itemCount)

}