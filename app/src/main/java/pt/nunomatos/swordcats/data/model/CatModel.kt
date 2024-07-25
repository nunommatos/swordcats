package pt.nunomatos.swordcats.data.model

import com.google.gson.annotations.SerializedName

data class CatModel(
    val breeds: List<CatBreedModel> = listOf(),
    val id: String = "",
    @SerializedName("url")
    val image: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val favoriteId: String? = null
) {
    fun getBreedName(): String {
        return breeds.first().name
    }

    fun getAverageLifeSpanText(): String {
        return breeds.first().lifeSpan
    }

    fun getAverageWeightText(): String {
        return breeds.first().getAverageWeight()
    }

    fun getAverageLifeSpan(): Pair<Int, Int> {
        return breeds.first().getAverageLifeSpan()
    }

    fun getTemperamentItems(): List<String> {
        return breeds.first().getTemperamentItems()
    }

    fun getDetailsUrl(): String? {
        return breeds.first().detailsUrl
    }

    fun getOrigin(): String {
        val breed = breeds.first()
        val country = breed.countryCode

        return StringBuilder()
            .append(String(Character.toChars(Character.codePointAt(country, 0) - 0x41 + 0x1F1E6)))
            .append(String(Character.toChars(Character.codePointAt(country, 1) - 0x41 + 0x1F1E6)))
            .append("   " + breed.origin)
            .toString()
    }

    fun getDescription(): String {
        return breeds.first().description
    }
}