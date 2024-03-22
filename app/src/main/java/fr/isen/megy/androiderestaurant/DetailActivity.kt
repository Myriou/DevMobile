package fr.isen.megy.androiderestaurant

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.megy.androiderestaurant.model.CartItem
import fr.isen.megy.androiderestaurant.model.Items
import fr.isen.megy.androiderestaurant.ui.theme.AndroidERestaurantTheme
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val item : Items = intent.getSerializableExtra("DISH") as Items

                    // Utiliser la fonction composable pour créer la barre d'applications
                    DishScreen(item, this@DetailActivity)
                }
            }
        }
    }


}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishScreen(dish: Items, context: Context) {


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(("Garfield's - " + (dish.nameFr)) ?: "Dish")
                },
                actions = {
                    IconButton(onClick = {
                        // Rediriger l'utilisateur vers l'écran du panier
                        context.startActivity(Intent(context, CartActivity::class.java))
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.cart),
                            contentDescription = "Panier"
                        )
                    }
                }
            )
        }
    ) {
            innerPadding ->
        DishDetails(innerPadding, dish, context)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DishDetails(innerPadding: PaddingValues, dish: Items, context: Context) {


    var quantity by remember { mutableStateOf(1) }
    val pricePerDish = dish.prices.first().price?.toFloat()
    val totalPrice = pricePerDish?.times(quantity)

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Carousel(dish)

        Text(
            text = dish.nameFr ?: "No name",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        FlowRow {
            dish.ingredients.forEach { ingName ->
                Text(
                    text = ingName.nameFr ?: "error",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            QuantitySelector(
                quantity = quantity,
                onQuantityChange = { newQuantity ->
                    if (newQuantity < 0)
                        quantity = 0
                    else
                        quantity = newQuantity
                }
            )
        }


            Button(
                onClick = {
                    addToCart(dish.nameFr ?: "No name", quantity, totalPrice ?: 0f, context)
                    AlertDialog.Builder(context)
                    .setTitle("Plat ajouté au panier")
                    .setMessage("Le plat a été ajouté à votre panier.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
                          },
                modifier = Modifier.fillMaxWidth()
            )      {
                Text(text = "Ajouter au panier - $totalPrice $")
            }


        }
    }

// Ajouter un élément au panier et le stocker au format JSON dans un fichier

const val CART_FILE_NAME = "panier.json"

fun addToCart(name: String, quantity: Int, totalPrice: Float, context: Context) {
    val cartItem = CartItem(name, quantity, totalPrice)
    val cartItemList = loadCartItems(context) // Charger la liste actuelle du panier
    cartItemList.add(cartItem)
    saveCartItems(cartItemList, context) // Enregistrer la liste mise à jour du panier
    // Compter le nombre total d'articles dans le panier
    val itemCount = cartItemList.sumBy { it.quantity }

    // Mettre à jour le nombre d'articles dans les préférences utilisateur
    updateCartItemCount(context, itemCount)

    Log.d("SaveCartItems", "addToCart: Item count updated to $itemCount")
}

fun updateCartItemCount(context: Context, itemCount: Int) {
    val sharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putInt("cartItemCount", itemCount)
        apply()
    }
}




fun loadCartItems(context: Context): MutableList<CartItem> {
    val cartFile = File(context.filesDir, CART_FILE_NAME)
    if (!cartFile.exists() || cartFile.length() == 0L) {
        Log.d("LoadCartItems", "Le fichier du panier n'existe pas ou est vide")
        return mutableListOf()
    }
    val json = cartFile.readText()
    Log.d("LoadCartItems", "Contenu du fichier JSON: $json")
    Log.d("LoadCartItems", "Cart file path: ${cartFile.absolutePath}")

    val cartItemsArray = Gson().fromJson(json, Array<CartItem>::class.java)
    val cartItemList = cartItemsArray.toMutableList()
    Log.d("LoadCartItems", "Liste des éléments du panier chargée avec succès")
    return cartItemList
}


fun saveCartItems(cartItems: List<CartItem>, context: Context) {
    Log.d("SaveCartItems", "Enregistrement de la liste des éléments du panier")
    val json = Gson().toJson(cartItems)

    val cartFile = File(context.filesDir, CART_FILE_NAME)
    cartFile.writeText(json)
    Log.d("SaveCartItems", "Liste des éléments du panier enregistrée avec succès: $json")
}




@Composable
fun QuantitySelector(quantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { onQuantityChange(quantity - 1) }) {
            Text(text = "-")
        }

        Text(text = "  $quantity  ")

        Button(onClick = { onQuantityChange(quantity + 1) }) {
            Text(text = "+")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel (dish: Items){

    Box(modifier = Modifier.fillMaxWidth()) {
        val pagerState = rememberPagerState(pageCount = { dish.images.size })
        val painter = rememberImagePainter(
            data = dish.images.getOrNull(pagerState.currentPage),

            builder = {
                crossfade(true)
                fallback(R.drawable.comingsoon)
                error(R.drawable.comingsoon)
            }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, bottom = 8.dp)
        ) { page ->
            Image(
                painter = painter, // Remplacer par la ressource de votre image
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }


    }
}

