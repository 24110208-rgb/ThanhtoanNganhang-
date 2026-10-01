package com.mycompany.thanhtoannganhang.model.dao;

import com.mycompany.thanhtoannganhang.model.entity.GiaoDich;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

/**
 * TANG MODEL - DAO Implementation cho GiaoDich.
 * Dung RESOURCE_LOCAL EntityManager (tuong thich Tomcat).
 */
@ApplicationScoped
public class GiaoDichDAOImpl implements GiaoDichDAO {

    private static final EntityManagerFactory EMF =
        Persistence.createEntityManagerFactory("my_persistence_unit");

    private EntityManager getEM() {
        return EMF.createEntityManager();
    }

    @Override
    public GiaoDich save(GiaoDich giaoDich) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(giaoDich);
            em.getTransaction().commit();
            return giaoDich;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<GiaoDich> findById(Long id) {
        EntityManager em = getEM();
        try {
            return Optional.ofNullable(em.find(GiaoDich.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<GiaoDich> findByMaGiaoDich(String maGiaoDich) {
        EntityManager em = getEM();
        try {
            GiaoDich g = em
                .createNamedQuery("GiaoDich.findByMaGiaoDich", GiaoDich.class)
                .setParameter("ma", maGiaoDich)
                .getSingleResult();
            return Optional.of(g);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<GiaoDich> findAll() {
        EntityManager em = getEM();
        try {
            return em.createNamedQuery("GiaoDich.findAll", GiaoDich.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<GiaoDich> findByTaiKhoanNguon(String soTaiKhoan) {
        EntityManager em = getEM();
        try {
            return em.createNamedQuery("GiaoDich.findByTaiKhoanNguon", GiaoDich.class)
                .setParameter("stk", soTaiKhoan)
                .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public GiaoDich update(GiaoDich giaoDich) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            GiaoDich merged = em.merge(giaoDich);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
