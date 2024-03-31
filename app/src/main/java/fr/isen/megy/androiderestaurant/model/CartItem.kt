package fr.isen.megy.androiderestaurant.model

data class CartItem(
    val dishName: String,
    var quantity: Int,
    var totalPrice: Float
)
