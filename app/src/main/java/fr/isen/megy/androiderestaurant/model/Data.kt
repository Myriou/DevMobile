package fr.isen.megy.androiderestaurant.model

import com.google.gson.annotations.SerializedName


data class Data (

    @SerializedName("name_fr" ) var nameFr : String?          = null,
    @SerializedName("items"   ) var items  : List<Items> = arrayListOf()

)


// new dataclass after GSON
data class Data2(val data: List<Category>)
data class Category(val nameFr: String, val items: List<Item>)
data class Item(
    val id: String,
    val nameFr: String,
    val nameEn: String,
    val idCategory: String,
    val categNameFr: String,
    val categNameEn: String,
    val images: List<String>,
    val ingredients: List<String>,
    val prices: List<Price>
)
data class Price(val amount: Double, val currency: String)