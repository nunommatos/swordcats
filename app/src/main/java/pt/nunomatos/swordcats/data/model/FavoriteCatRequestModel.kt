package pt.nunomatos.swordcats.data.model

import com.google.gson.annotations.SerializedName

class FavoriteCatRequestModel(
    @SerializedName("image_id")
    val catId: String,
    @SerializedName("sub_id")
    val userId: String
)