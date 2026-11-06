package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.UsuarioEntity
import com.DanielDMikelG.kalo.domain.model.Usuario

fun UsuarioEntity.toDomain() = Usuario(
    id = id,
    email = email,
    nombre = nombre,
    fechaNacimiento = fechaNacimiento,
    sexo = sexo,
    nivelActividad = nivelActividad,
    altura = altura
)

fun Usuario.toEntity() = UsuarioEntity(
    id = id,
    email = email,
    nombre = nombre,
    fechaNacimiento = fechaNacimiento,
    sexo = sexo,
    nivelActividad = nivelActividad,
    altura = altura
)