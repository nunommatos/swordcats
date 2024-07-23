package pt.nunomatos.swordcats.data.model

import com.google.gson.annotations.SerializedName

class CatBreedModel(
    val name: String,
    @SerializedName("life_span")
    val lifeSpan: String,
    val weight: CatWeightModel,
    val temperament: String,
    @SerializedName("country_code")
    val countryCode: String,
    val origin: String,
    val description: String,
    @SerializedName("wikipedia_url")
    val detailsUrl: String?
) {
    fun getAverageWeight(): String {
        return weight.metric
    }

    fun getAverageLifeSpan(): Pair<Int, Int> {
        val lifeSpanAges = lifeSpan.split(" - ").map { it.toIntOrNull() ?: 0 }
        return lifeSpanAges.first() to lifeSpanAges.last()
    }

    fun getTemperamentItems(): List<String> {
        return temperament.split(", ")
    }
}