package pt.nunomatos.swordcats.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pt.nunomatos.swordcats.data.model.TABLE_NAME_USER
import pt.nunomatos.swordcats.data.model.UserModel

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserModel): Long

    @Update
    suspend fun update(user: UserModel): Int

    @Query("SELECT * FROM $TABLE_NAME_USER WHERE id=:id")
    suspend fun getById(id: String): UserModel?

    @Query("SELECT * FROM $TABLE_NAME_USER WHERE email=:email")
    suspend fun getByEmail(email: String): UserModel?
}