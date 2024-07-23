package pt.nunomatos.swordcats.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pt.nunomatos.swordcats.data.model.UserModel
import javax.inject.Singleton

@Singleton
@Database(
    entities = [UserModel::class],
    exportSchema = false,
    version = 1,
)

@TypeConverters(Converters::class)
abstract class CatsDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {

        private const val DATABASE_NAME = "cats"

        @Volatile
        private var instance: CatsDatabase? = null

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, CatsDatabase::class.java, DATABASE_NAME).build()

        fun getDatabase(context: Context): CatsDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
    }
}