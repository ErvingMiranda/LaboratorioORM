package ni.edu.uam.laboratorioorm.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import ni.edu.uam.laboratorioorm.database.JPAUtil;
import ni.edu.uam.laboratorioorm.model.Equipo;
import ni.edu.uam.laboratorioorm.model.TipoEquipo;

import java.util.List;

public class EquipoDAO {

    // Obtiene el tipo gestionado por el EntityManager actual.
    private TipoEquipo obtenerTipoEquipo(EntityManager em, Equipo equipo) {
        if (equipo.getTipoEquipo() == null || equipo.getTipoEquipo().getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de equipo.");
        }

        Long idTipo = equipo.getTipoEquipo().getId();
        TipoEquipo tipo = em.find(TipoEquipo.class, idTipo);

        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de equipo seleccionado no existe.");
        }

        return tipo;
    }

    // Guarda un equipo relacionado con un tipo existente.
    public void guardar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            TipoEquipo tipo = obtenerTipoEquipo(em, equipo);
            equipo.setTipoEquipo(tipo);

            em.persist(equipo);
            tx.commit();

        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;

        } finally {
            em.close();
        }
    }

    // Actualiza los datos de un equipo existente.
    public void actualizar(Equipo equipo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            if (equipo.getId() == null) {
                throw new IllegalArgumentException("El equipo no tiene un ID.");
            }

            if (em.find(Equipo.class, equipo.getId()) == null) {
                throw new IllegalArgumentException("El equipo ya no existe en la base de datos.");
            }

            TipoEquipo tipo = obtenerTipoEquipo(em, equipo);
            equipo.setTipoEquipo(tipo);

            em.merge(equipo);
            tx.commit();

        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;

        } finally {
            em.close();
        }
    }

    // Elimina un equipo utilizando su identificador.
    public boolean eliminar(long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Equipo equipo = em.find(Equipo.class, id);

            if (equipo == null) {
                tx.rollback();
                return false;
            }

            em.remove(equipo);
            tx.commit();
            return true;

        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;

        } finally {
            em.close();
        }
    }

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

    // Busca equipos por nombre, tipo o ambos criterios.
    public List<Equipo> buscar(String nombre, TipoEquipo tipo) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo WHERE 1=1"
            );

            if (nombre != null && !nombre.isBlank()) {
                jpql.append(" AND LOWER(e.nombre) LIKE :nombre");
            }

            if (tipo != null) {
                jpql.append(" AND e.tipoEquipo.id = :idTipo");
            }

            jpql.append(" ORDER BY e.id");

            TypedQuery<Equipo> consulta = em.createQuery(
                    jpql.toString(), Equipo.class
            );

            // Asigna los valores a los parámetros de la consulta.
            if (nombre != null && !nombre.isBlank()) {
                consulta.setParameter("nombre", "%" + nombre.trim().toLowerCase() + "%");
            }

            if (tipo != null) {
                consulta.setParameter("idTipo", tipo.getId());
            }

            return consulta.getResultList();

        } finally {
            em.close();
        }
    }
}
