package fr.isen.megy.androiderestaurant.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Dishes (

    @SerializedName("data" ) var data : List<Data> = arrayListOf()

): Serializable
