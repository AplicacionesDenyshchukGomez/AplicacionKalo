# AplicacionKalo
Aplicación móvil de nutrición y conteo de calorías, desarrollada en Kotlin.<br><br>

Descripción<br>
Kalo es una app que hace que llevar el control de la alimentación sea rápido y sencillo. En lugar de buscar y pesar cada alimento a mano, el usuario registra sus comidas con una foto, la voz o el texto, y la app estima calorías y macronutrientes y le ofrece recomendaciones personalizadas, con un enfoque en los hábitos y sin generar obsesión por los números.
Proyecto desarrollado en el marco de Proyecto Moviles.<br><br>

Características principales<br>
Registro de comidas por foto con estimación de alimentos, porciones y macros.<br>
Buscador de alimentos y escáner de código de barras.<br>
Diario diario con calorías restantes, macros y registro de agua.<br>
Objetivos personalizados según edad, peso, altura, actividad y meta (perder, mantener o ganar peso).<br>
Seguimiento del progreso: evolución del peso y resúmenes semanales.<br><br>

Tecnologías utilizadas<br>
Lenguaje ->	Kotlin<br>
Plataforma	-> Android<br>
Interfaz	-> Jetpack Compose<br>
Arquitectura	-> MVVM<br>
Base de datos local	-> Room (SQLite)<br>
Base de datos de alimentos	-> Open Food Facts<br>
Control de versiones	-> Git y GitHub<br><br>
Autores<br>
Mikel — @Mikel1412<br>
Daniel — @DaniZomboo<br>
<br>
<br>
PLANIFICACION FUTURO PROYECTO
PARA MAS TARDE

ayuno, preferncia_dieta, alimento_dieta, logro, usuario_logro


LO VAMOS VIENDO

alimento_alergeno, mision_diaria


PONER 100%

Usuario, Objetivo, registro_peso, registro_agua, comida, entrada_comida, alimento, racha


COMO SE COMPARTEN LOS DATOS?

//Para escribir

1. El ViewModel tiene un usuario, por ejemplo, y se lo pasa al repositorio
2. El repositorio lo convierte con el mapper y llama al DAO
	2.1 El mapper lo transforma en UsuarioEntity
3. El dao guarda la entidad en la base de datos
4. KaloDatabase.kt escribe la fila en kalo.db

//Para leer
Seria justo igual que los pasos para escribir pero al reves


ISSUES A HACER<br>
Mikel<br>
Issue CreacionUsuarioYRelacionadosRoom -> Usuario, Objetivo, RestiroPeso, RegistroAgua, Racha<br>
Issue CreacionComidaYRelacionadosRoom -> Comida, EntradaComida, Alimento<br><br>
Dani<br>
Issue DomainModelUsuario -> Usuario, Objetivo, RestiroPeso, RegistroAgua, Racha<br>
Issue DomainModelComida -> Comida, EntradaComida, Alimento<br>
Issue PantallaUsuario -> Hacer la pantalla de perfil y conectar con domain model<br>
