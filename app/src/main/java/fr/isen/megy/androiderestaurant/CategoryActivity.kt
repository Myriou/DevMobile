package fr.isen.megy.androiderestaurant
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.megy.androiderestaurant.model.Dishes
import fr.isen.megy.androiderestaurant.model.Item
import fr.isen.megy.androiderestaurant.model.Items
import fr.isen.megy.androiderestaurant.ui.theme.AndroidERestaurantTheme
import org.json.JSONObject
class CategoryActivity : ComponentActivity() {
    var mutableDataList by mutableStateOf(emptyList<Items>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val category = intent.getStringExtra("category") ?: ""
            fetchDishData(category)
            AndroidERestaurantTheme {
                // Utiliser la fonction composable pour créer la barre d'applications
                CategoryScreen(mutableDataList, category) { dishName ->
                    navigateToDetailActivity(dishName)
                }
            }
        }
    }
    private fun fetchDishData(category: String) {
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)

        val url = "http://test.api.catering.bluecodegames.com/menu"
        val requestBody = JSONObject().apply {
            put("id_shop", "1")
        }.toString()

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val menuResponse = Gson().fromJson(response, Dishes::class.java)
                    val categoryDish = menuResponse.data.find { it.nameFr == category }
                    val items = categoryDish?.items




                    val itemsList = items?.map { Items(it.id, it.nameFr, it.idCategory, it.categNameFr, it.images, it.ingredients, it.prices) }

//

                    mutableDataList = itemsList ?: emptyList()
                    Log.d("GSON", "test outside: $mutableDataList")
                } catch (e: Exception) {
                    Log.e("DISHES", "Error: ${e.toString()}")
                }
            },
            Response.ErrorListener { error ->
                Log.e("DISHES", "Error: ${error.toString()}")
            }
        ) {
            override fun getBody(): ByteArray = requestBody.toByteArray()
            override fun getBodyContentType(): String = "application/json; charset=utf-8"
        }

        queue.add(stringRequest)
    }





    private fun navigateToDetailActivity(dishName: Items) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("DISH", dishName)
        }
        startActivity(intent)
    }

}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(dishes: List<Items>,category: String, onItemClick: (Items) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Garfield's - " +category)
                }
            )
        }
    ) {
            innerPadding ->
        ScrollContent(innerPadding,dishes, onItemClick )


    }
}


@Composable
fun ScrollContent(innerPadding: PaddingValues, dishList: List<Items>, onItemClick: (Items) -> Unit) {
    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        items(dishList) { dish ->
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    TextButton(
                        onClick = { onItemClick(dish) },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text(
                            text = dish.nameFr ?: "No name",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Black
                        )
                    }
                    Image(

                        painter = rememberImagePainter(
                            data = dish.images.firstOrNull(),
                            builder = {
                                crossfade(true) // Enable crossfade animation
                                placeholder(R.drawable.comingsoon)
                                if (dish.images.size > 1) {    //
                                    dish.images.drop(1).forEach {
                                        data(it)
                                    }

                                }
                                //s'il y a une erreur drop la première image et prend la deuxième
                                error(R.drawable.comingsoon)
                                Log.d(
                                    "TEST",
                                    "Taille de la liste d'images: ${dish.images.size}"
                                )
                            }
                        ),
                        contentDescription = "Dish Image", // Content description for accessibility
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp) // Adjust the size and padding
                    )
                    Text(
                        text = (dish.prices.firstOrNull()?.price + "$") ?: "Price not available",
                        color = Color.Black,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                }
            }
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