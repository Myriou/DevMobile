package fr.isen.megy.androiderestaurant.model

data class CartItem(
    val dishName: String,
    val quantity: Int,
    val totalPrice: Float
)
