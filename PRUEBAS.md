# Pruebas del laboratorio ORM

Las pruebas se ejecutaron el 12 de noviembre de 2026 con Java 21, JavaFX 21.0.6, Hibernate ORM 7.0.4.Final y PostgreSQL 18.3. La aplicación compiló con `.\mvnw.cmd verify`. Los casos se ejecutaron sobre la vista FXML real, accionando sus controles desde JavaFX y comprobando las escrituras con un nuevo `EntityManager` o con `psql`. Las capturas muestran las ventanas reales y se tomaron sin el puntero.

| ID | Entrada o procedimiento | Resultado esperado | Resultado obtenido | Evidencia | Estado |
| --- | --- | --- | --- | --- | --- |
| P1 | Guardar `QA-ORM-261112`, Adaptador de prueba, marca Pruebas, cantidad 2, tipo Redes y estado Disponible. | Se guarda y aparece en la tabla. | La aplicación confirmó el guardado. La tabla y una consulta nueva a PostgreSQL mostraron el equipo con esos datos. | [Registro](evidencias/P1_Registro.png) | Aprobada |
| P2 | Intentar guardar otro equipo con `QA-ORM-261112` y nombre «Intento duplicado». | Se rechaza el duplicado con una alerta y se conserva un solo registro con ese código. | PostgreSQL devolvió `23505` y la aplicación mostró «Ya existe un equipo con ese código». El primer registro conservó sus datos y la aplicación siguió abierta. | [Alerta de duplicado](evidencias/P2_CodigoDuplicado.png) | Aprobada |
| P3 | Seleccionar el equipo temporal y cambiar el nombre a «Adaptador de prueba actualizado», el estado a En mantenimiento, la cantidad a 4 y el tipo a Periféricos. Pulsar Actualizar. | Los cambios se ven en la tabla y quedan guardados. | Se confirmó la actualización y `psql` devolvió los cuatro valores nuevos para el mismo código. | [Actualización](evidencias/P3_Actualizacion.png), [consulta PostgreSQL](evidencias/PostgreSQL.txt) | Aprobada |
| P4 | Después de P8, seleccionar el equipo temporal, pulsar Eliminar y cancelar. Repetir y aceptar la confirmación. | Cancelar conserva el equipo. Aceptar lo elimina de la tabla y de PostgreSQL. | Al cancelar, el registro seguía visible y persistido. Al aceptar, apareció el aviso de éxito, la tabla volvió a ocho filas y el código temporal dejó de existir en PostgreSQL. | [Confirmación](evidencias/P4_Confirmacion.png), [listado final](evidencias/P4_Eliminacion.png), [consulta PostgreSQL](evidencias/PostgreSQL.txt) | Aprobada |
| P5 | Escribir `pUtAdOrA` en la búsqueda por nombre y aplicar sin elegir tipo. | Aparecen solo las coincidencias parciales, sin distinguir mayúsculas. | Aparecieron `LAB-001` (Computadora OptiPlex) y `LAB-003` (Computadora ProDesk). | [Búsqueda parcial](evidencias/P5_BusquedaParcial.png) | Aprobada |
| P6 | Filtrar por Computadoras. Luego combinar ese tipo con `pRoDeSk`. Repetir el fragmento con el tipo Redes. | El filtro muestra solo el tipo elegido. La combinación exige que coincidan ambos criterios. | El tipo solo devolvió `LAB-001`, `LAB-002` y `LAB-003`. La combinación con Computadoras devolvió `LAB-003`. Con Redes devolvió cero filas. Limpiar los filtros recuperó el listado. | [Filtro por tipo](evidencias/P6_FiltroTipo.png), [filtro combinado](evidencias/P6_FiltroCombinado.png) | Aprobada |
| P7 | Intentar guardar `QA-NEG-261112` con cantidad `-1` y los demás campos válidos. | La aplicación rechaza la cantidad y no guarda el equipo. | Apareció «La cantidad no puede ser negativa» y la consulta a PostgreSQL devolvió cero filas para ese código. | [Alerta de cantidad negativa](evidencias/P7_CantidadNegativa.png), [consulta PostgreSQL](evidencias/PostgreSQL.txt) | Aprobada |
| P8 | Cerrar por completo la aplicación después de P3 y abrir un proceso nuevo antes de eliminar el equipo temporal. | Se recupera el equipo con los cambios confirmados. | El nuevo proceso mostró «Adaptador de prueba actualizado», cantidad 4, estado En mantenimiento y tipo Periféricos. | [Listado tras reiniciar](evidencias/P8_PersistenciaReinicio.png) | Aprobada |

También se rechazaron un código vacío, un nombre vacío, la cantidad `abc`, un equipo sin tipo y el estado `Prestado`. Al actualizar el equipo temporal con el código existente `LAB-001`, la aplicación mostró la alerta de duplicado y ambos registros conservaron sus datos.

## Integridad y rollback

P2 provocó una violación de la restricción única `equipo_codigo_key` durante el guardado. Para comprobar que `rollback()` deshace escrituras previas de la misma transacción, se abrió un `EntityManager`, se ejecutó `begin()` y se insertó `QA-RB-261112` con `persist()` y `flush()`. Un segundo `persist()` con `LAB-001` produjo el error `23505`. Se ejecutó `rollback()` y, en una consulta con otro `EntityManager`, se obtuvo cero filas para `QA-RB-261112` y una para `LAB-001`. [Salida de consola](evidencias/Rollback.txt)

La consulta final de PostgreSQL coincidió con la inicial en todas las columnas de los ocho equipos, de `LAB-001` a `LAB-008`. [Comparación de PostgreSQL](evidencias/PostgreSQL.txt)

El script SQL también se ejecutó en una base temporal vacía y creó los tres tipos y los ocho equipos iniciales.

[Repositorio del laboratorio](https://github.com/ErvingMiranda/LaboratorioORM)
