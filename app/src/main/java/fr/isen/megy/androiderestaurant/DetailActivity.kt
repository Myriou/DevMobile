package fr.isen.megy.androiderestaurant

import android.os.Bundle
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import fr.isen.megy.androiderestaurant.model.Items
import fr.isen.megy.androiderestaurant.ui.theme.AndroidERestaurantTheme

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
                    DishScreen(item)
                }
            }
        }
    }


}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishScreen(dish: Items) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(("Garfield's - " + (dish.nameFr)) ?: "Dish")
                }
            )
        }
    ) {
            innerPadding ->
        DishDetails(innerPadding, dish)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DishDetails(innerPadding: PaddingValues,dish: Items) {
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
                    modifier = Modifier
                        .padding(8.dp)



                )
            }
        }
        Text(
            text = ("Prix : " + dish.prices.firstOrNull()?.price + "$") ?: "Price not available",
            fontSize = 20.sp
        )



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
                    .height(200.dp)
            )
        }


    }
}