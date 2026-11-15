package com.DanielDMikelG.kalo.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.mapper.toEntity
import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.NivelActividad
import com.DanielDMikelG.kalo.domain.model.OrigenAlimento
import com.DanielDMikelG.kalo.domain.model.Sexo
import com.DanielDMikelG.kalo.domain.model.Usuario
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import java.time.LocalDate

abstract class BaseDbTest {

    protected lateinit var db: KaloDatabase
    protected var usuarioId = 0L

    @Before
    fun crearBaseDeDatos() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // En memoria: nace vacía en cada test y desaparece al cerrarla
        db = Room.inMemoryDatabaseBuilder(context, KaloDatabase::class.java).build()

        // Las tablas dependen de usuario por clave foránea, así que hace falta uno
        usuarioId = runBlocking {
            db.usuarioDao().insertar(
                Usuario(
                    email = "test@kalo.com",
                    nombre = "Test",
                    fechaNacimiento = LocalDate.of(2000, 1, 1),
                    sexo = Sexo.HOMBRE,
                    nivelActividad = NivelActividad.MODERADO,
                    altura = 175
                ).toEntity()
            )
        }
    }

    @After
    fun cerrarBaseDeDatos() = db.close()

    protected fun pollo(codigoBarras: String? = null) = Alimento(
        codigoBarras = codigoBarras,
        nombre = "Pollo",
        kcal100g = 200f, prot = 20f, carb = 0f, grasa = 10f, fibra = 0f,
        origen = OrigenAlimento.MANUAL
    )
}