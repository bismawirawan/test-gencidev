package com.layanacomputindo.bprmsa.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery

abstract class Database : RoomDatabase() {

    fun clearAndResetAllTables() {
        // reset all auto-incrementalValues
        val query = SimpleSQLiteQuery("DELETE FROM sqlite_sequence")

        runInTransaction {
            clearAllTables()
            query(query)
        }
    }

}