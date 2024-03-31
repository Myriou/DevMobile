package fr.isen.megy.androiderestaurant

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.megy.androiderestaurant.model.CartItem
import fr.isen.megy.androiderestaurant.ui.theme.AndroidERestaurantTheme
import java.io.File

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                AutoUpdate(this)
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val cartItems = loadCartItemsFromJson(this)
                    val itemCount = cartItems.sumBy { it.quantity }

                    // Mettre à jour le nombre d'articles dans les préférences utilisateur
                    updateCartItemCount(this, itemCount)
                    SmallTopAppBar(this) { }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(context : Context, onCategoryClick: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Garfield's Panier",
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, HomeActivity::class.java))
                    })
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
        CartContent(innerPadding)

    }
}


@Composable
fun CartContent(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val cartItems = remember { loadCartItemsFromJson(context = context) }


    Column(
        modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ) {
        // Afficher chaque article du panier
        cartItems.forEach { cartItem ->
            CartItemRow(context,cartItem = cartItem)
        }

        // Bouton pour passer la commande
        Button(
            onClick = { /* Action à effectuer pour passer la commande */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(text = "Passer la commande")
        }
    }
}

// Fonction pour supprimer un article du panier
fun removeCartItem(context: Context, cartItem: CartItem) {
    val cartItems = loadCartItemsFromJson(context)
    cartItems.remove(cartItem)
    saveCartItems(cartItems = cartItems, context = context)

}


fun loadCartItemsFromJson(context: Context): MutableList<CartItem> {
    val cartFile = File(context.filesDir, CART_FILE_NAME)
    return if (cartFile.exists()) {
        val json = cartFile.readText()
        Gson().fromJson(json, object : TypeToken<MutableList<CartItem>>() {}.type)
    } else {
        mutableListOf()
    }
}


@Composable
fun CartItemRow(
    context: Context,
    cartItem: CartItem,

) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Afficher les détails de l'article du panier
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = cartItem.dishName)
            Text(text = "Quantité: ${cartItem.quantity}")
            Text(text = "Prix total: ${cartItem.totalPrice}")
        }

        // Bouton pour supprimer l'article du panier
        IconButton(
            onClick = {
                removeCartItem(context, cartItem)
                AlertDialog.Builder(context)
                    .setTitle("Plat supprimé")
                    .setMessage("Le plat a été supprimé de votre panier.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                        context.startActivity(Intent(context, CartActivity::class.java))}
                    .show()


            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.poubelle),
                contentDescription = "Supprimer"
            )
        }
    }
}
