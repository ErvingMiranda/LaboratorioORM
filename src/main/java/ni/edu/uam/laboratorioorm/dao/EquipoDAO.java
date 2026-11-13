package ni.edu.uam.laboratorioorm.dao;

import jakarta.persistence.EntityManager;
import ni.edu.uam.laboratorioorm.database.JPAUtil;
import ni.edu.uam.laboratorioorm.model.Equipo;

import java.util.List;

public class EquipoDAO {

    // Consulta los equipos junto con sus tipos.
    public List<Equipo> listar() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT e FROM Equipo e " +
                            "JOIN FETCH e.tipoEquipo " +
                            "ORDER BY e.id",
                    Equipo.class
            ).getResultList();

        } finally {
            em.close();
        }
    }
}
