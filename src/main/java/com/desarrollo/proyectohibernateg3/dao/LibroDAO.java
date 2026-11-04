package com.desarrollo.proyectohibernateg3.dao;

import com.desarrollo.proyectohibernateg3.database.JPAUtil;
import com.desarrollo.proyectohibernateg3.model.Libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.hibernate.mapping.List;

public class LibroDAO {
    public void guardar(Libro libro){
        EntityManager em = JPAUtil.getEntityManagerFactory();
        EntityTransaction tx = em.getTransaction();

        try{
            // Iniciar la transacci[on en PostgreSQL
            tx.begin();
            // Decirle a Hibernate que prepare la persistencia del objeto
            em.persist(libro);
            // Confirmar los cambios en la base de datos
            tx.commit();
        } catch(RuntimeException ex){
            if(tx.isActive()){
                tx.rollback();
            }
            throw ex;
        } finally{
            em.close();
        }
    }

    public List<Libro> {
        EntityManager em = JPAUtil.getEntityManagerFactory();
        try{
            return em.createQuery("SELECT 1 FROM Libro l" +
                    "ORDER BY l.id", Libro.class).getResultList();
        } finally{
            em.close();
        }
    }
}
