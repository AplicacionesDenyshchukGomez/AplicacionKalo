package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.UsuarioEntity

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