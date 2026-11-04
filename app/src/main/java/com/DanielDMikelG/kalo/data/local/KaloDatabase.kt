package com.DanielDMikelG.kalo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.DanielDMikelG.kalo.data.local.dao.ObjetivoDao
import com.DanielDMikelG.kalo.data.local.dao.UsuarioDao
import com.DanielDMikelG.kalo.data.local.entity.ObjetivoEntity
import com.DanielDMikelG.kalo.data.local.entity.UsuarioEntity

@Database(
    entities = [UsuarioEntity::class, ObjetivoEntity::class],
    version = 3
)
abstract class KaloDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun objetivoDao(): ObjetivoDao

    companion object {
        @Volatile
        private var INSTANCE: KaloDatabase? = null

        fun obtener(context: Context): KaloDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KaloDatabase::class.java,
                    "kalo.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true) // solo en desarrollo
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
