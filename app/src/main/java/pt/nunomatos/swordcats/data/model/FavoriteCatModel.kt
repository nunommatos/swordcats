package pt.nunomatos.swordcats.data.model

import com.google.gson.annotations.SerializedName

class FavoriteCatModel(
    val id: String,
    @SerializedName("image_id")
    val catId: String
)