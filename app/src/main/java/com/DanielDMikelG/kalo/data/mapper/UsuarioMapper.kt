package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.UsuarioEntity
import com.DanielDMikelG.kalo.domain.model.Usuario
import java.time.LocalDate

fun UsuarioEntity.toDomain() = Usuario(
    id = id,
    email = email,
    nombre = nombre,
    fechaNacimiento = LocalDate.parse(fechaNacimiento),
    sexo = sexo,
    nivelActividad = nivelActividad,
    altura = altura
)

fun Usuario.toEntity() = UsuarioEntity(
    id = id,
    email = email,
    nombre = nombre,
    fechaNacimiento = fechaNacimiento.toString(),
    sexo = sexo,
    nivelActividad = nivelActividad,
    altura = altura
)