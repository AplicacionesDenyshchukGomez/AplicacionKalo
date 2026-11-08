package com.DanielDMikelG.kalo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.DanielDMikelG.kalo.data.local.dao.AlimentoDao
import com.DanielDMikelG.kalo.data.local.dao.ComidaDao
import com.DanielDMikelG.kalo.data.local.dao.EntradaComidaDao
import com.DanielDMikelG.kalo.data.local.dao.ObjetivoDao
import com.DanielDMikelG.kalo.data.local.dao.RachaDao
import com.DanielDMikelG.kalo.data.local.dao.RegistroAguaDao
import com.DanielDMikelG.kalo.data.local.dao.RegistroPesoDao
import com.DanielDMikelG.kalo.data.local.dao.UsuarioDao
import com.DanielDMikelG.kalo.data.local.entity.AlimentoEntity
import com.DanielDMikelG.kalo.data.local.entity.ComidaEntity
import com.DanielDMikelG.kalo.data.local.entity.EntradaComidaEntity
import com.DanielDMikelG.kalo.data.local.entity.ObjetivoEntity
import com.DanielDMikelG.kalo.data.local.entity.RachaEntity
import com.DanielDMikelG.kalo.data.local.entity.RegistroAguaEntity
import com.DanielDMikelG.kalo.data.local.entity.RegistroPesoEntity
import com.DanielDMikelG.kalo.data.local.entity.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class, ObjetivoEntity::class,
        RegistroPesoEntity::class, RegistroAguaEntity::class, RachaEntity::class,
        AlimentoEntity::class, ComidaEntity::class, EntradaComidaEntity::class
    ],
    version = 1
)
abstract class KaloDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun objetivoDao(): ObjetivoDao
    abstract fun registroPesoDao(): RegistroPesoDao
    abstract fun registroAguaDao(): RegistroAguaDao
    abstract fun rachaDao(): RachaDao

    abstract fun alimentoDao(): AlimentoDao

    abstract fun comidaDao(): ComidaDao

    abstract fun entradaComidaDao(): EntradaComidaDao

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
