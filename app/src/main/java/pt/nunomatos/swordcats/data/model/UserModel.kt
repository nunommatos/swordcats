package pt.nunomatos.swordcats.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

const val TABLE_NAME_USER = "user"

@Entity(tableName = TABLE_NAME_USER)
data class UserModel(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val userFeed: UserFeedModel? = null
)