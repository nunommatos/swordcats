package pt.nunomatos.swordcats.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

data class UserFeedModel(
    val feedPage: Int,
    val updatedAt: Long,
    val isLocalFeed: Boolean,
    val cats: List<CatModel>
) {
    companion object {
        fun fromNow(feedPage: Int, isLocalFeed: Boolean, cats: List<CatModel>): UserFeedModel {
            return UserFeedModel(
                feedPage = feedPage,
                updatedAt = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis,
                isLocalFeed = isLocalFeed,
                cats = cats
            )
        }
    }

    fun getUpdatedDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy, HH:mm")
        return dateFormat.format(updatedAt)
    }
}