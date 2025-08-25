package test.gencidev.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import test.gencidev.database.dao.DaoData
import test.gencidev.database.entity.DayOffEntity

@Database(entities = [DayOffEntity::class], version = 1, exportSchema = false)
abstract class DataDb: RoomDatabase() {
    companion object Companion {
        private var instance: DataDb? = null

        fun getDb(context: Context): DataDb {
            if (instance == null){
                instance = Room.databaseBuilder(context.applicationContext, DataDb::class.java, "DataDb")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as DataDb
        }
    }

    abstract fun daoData(): DaoData
}