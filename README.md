# Laboratorio evaluado: Control de equipos de laboratorio

**Estudiante:** Erving Miranda  
**Docente:** Freddy Luis López Barrios  
**Asignatura:** Programación de Aplicaciones de Escritorio  
**Fecha:** 12 de noviembre de 2026

## Descripción

Aplicación de escritorio para administrar equipos de laboratorio. Permite registrar, consultar, actualizar y eliminar equipos, además de realizar búsquedas por nombre y filtros por tipo.

## Tecnologías utilizadas

- Java 21 y JavaFX con FXML.
- Hibernate ORM y Jakarta Persistence.
- PostgreSQL.
- Maven.

## Ejecución del proyecto

1. Abrir `sql/ScriptLaboratorioORM.sql` en pgAdmin y ejecutar la instrucción `CREATE DATABASE` desde la base `postgres`.
2. Conectarse a `bd_laboratorio_orm` y ejecutar el resto del script para crear el esquema, las tablas y los registros iniciales.
3. Abrir el proyecto en IntelliJ IDEA utilizando JDK 21 y esperar a que Maven cargue las dependencias.
4. En **Run → Edit Configurations**, configurar la variable de entorno `BD_PASSWORD` con la contraseña local de PostgreSQL.
5. Ejecutar la clase `LaboratorioApplication`.

La conexión utiliza el usuario `postgres` y el puerto `5432`, definidos en `persistence.xml`.

## Entidades y persistencia

La aplicación utiliza dos entidades: `Equipo` y `TipoEquipo`. Cada equipo pertenece a un tipo mediante una relación `@ManyToOne`, mientras que un tipo puede tener varios equipos.

La persistencia se administra con Hibernate mediante los DAO, utilizando `EntityManager` y transacciones para las operaciones CRUD. Las búsquedas se realizan mediante consultas JPQL parametrizadas.

## Pruebas

Se realizaron las ocho pruebas requeridas, incluyendo operaciones CRUD, validaciones, filtros, persistencia y comprobación de `rollback()`.

Los resultados y las capturas se encuentran en [Pruebas y evidencias](PRUEBAS.md).