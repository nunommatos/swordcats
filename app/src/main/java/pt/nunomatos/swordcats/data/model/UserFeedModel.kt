package pt.nunomatos.swordcats.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

data class UserFeedModel(
    val feedPage: Int = 0,
    val updatedAt: Long = 0L,
    val cats: List<CatModel> = listOf()
) {
    companion object {
        fun fromNow(feedPage: Int, cats: List<CatModel>): UserFeedModel {
            return UserFeedModel(
                feedPage = feedPage,
                updatedAt = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis,
                cats = cats
            )
        }
    }

    fun getUpdatedDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault())
        return dateFormat.format(updatedAt)
    }
}