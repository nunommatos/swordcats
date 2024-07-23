package pt.nunomatos.swordcats.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pt.nunomatos.swordcats.data.model.CatModel
import pt.nunomatos.swordcats.data.model.UserFeedModel


class Converters {
    private val gson: Gson = Gson()

    @TypeConverter
    fun userFeedToString(userFeed: UserFeedModel?): String? {
        return if (userFeed != null) {
            gson.toJson(userFeed)
        } else {
            ""
        }
    }

    @TypeConverter
    fun stringToUserFeed(str: String?): UserFeedModel? {
        return if (!str.isNullOrBlank()) {
            gson.fromJson(str, object : TypeToken<UserFeedModel>() {}.type)
        } else {
            null
        }
    }

    @TypeConverter
    fun catToString(cat: CatModel): String {
        return gson.toJson(cat)
    }

    @TypeConverter
    fun stringToCat(str: String): CatModel {
        return gson.fromJson(str, object : TypeToken<CatModel>() {}.type)
    }
}