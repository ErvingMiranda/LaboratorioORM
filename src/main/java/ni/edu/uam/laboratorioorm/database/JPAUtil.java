package ni.edu.uam.laboratorioorm.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    // Fábrica compartida para gestionar la persistencia.
    private static EntityManagerFactory entityManagerFactory;

    // Inicializa Hibernate una sola vez.
    public static synchronized void iniciar() {
        if (entityManagerFactory == null) {

            // Obtiene la contraseña desde una variable de entorno.
            String password = System.getenv("BD_PASSWORD");

            if (password == null || password.isBlank()) {
                throw new IllegalStateException(
                        "La variable BD_PASSWORD no está configurada."
                );
            }

            // Configura la contraseña de conexión.
            Map<String, Object> propiedades = new HashMap<>();
            propiedades.put("jakarta.persistence.jdbc.password", password);

            // Carga la unidad de persistencia definida en persistence.xml.
            entityManagerFactory = Persistence.createEntityManagerFactory(
                    "laboratorioPU", propiedades
            );
        }
    }

    // Crea un EntityManager para trabajar con la base de datos.
    public static EntityManager getEntityManager() {
        iniciar();
        return entityManagerFactory.createEntityManager();
    }

    // Libera los recursos de Hibernate al cerrar la aplicación.
    public static synchronized void cerrar() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }
}
