package test.gencidev.module

import android.content.Context
import androidx.room.Room
import com.layanacomputindo.bprmsa.database.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import test.gencidev.database.DataDb
import test.gencidev.database.dao.DaoData
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesRoom(@ApplicationContext context: Context): DataDb {
        return Room.databaseBuilder(context, DataDb::class.java, "test-gencidev.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDataDao(appDatabase: DataDb): DaoData {
        return appDatabase.daoData()
    }

}