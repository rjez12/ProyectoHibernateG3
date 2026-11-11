package com.desarrollo.proyectohibernateg3.dao;

import com.desarrollo.proyectohibernateg3.database.JPAUtil;
import com.desarrollo.proyectohibernateg3.model.Categoria;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select c from Categoria c", Categoria.class).getResultList();
        }  finally {
            em.close();
        }

    }
}

