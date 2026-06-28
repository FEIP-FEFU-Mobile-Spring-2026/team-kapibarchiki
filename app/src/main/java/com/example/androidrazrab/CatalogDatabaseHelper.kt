package com.example.androidrazrab

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CatalogDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE catalog_cache (
                id INTEGER PRIMARY KEY,
                json TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS catalog_cache")
        onCreate(db)
    }

    fun saveCatalog(json: String) {
        writableDatabase.execSQL(
            "INSERT OR REPLACE INTO catalog_cache(id, json) VALUES(1, ?)",
            arrayOf(json)
        )
    }

    fun getCatalogJson(): String? {
        val cursor = readableDatabase.rawQuery(
            "SELECT json FROM catalog_cache WHERE id = 1",
            null
        )

        cursor.use {
            return if (it.moveToFirst()) {
                it.getString(0)
            } else {
                null
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "catalog_cache.db"
        private const val DATABASE_VERSION = 1
    }
}