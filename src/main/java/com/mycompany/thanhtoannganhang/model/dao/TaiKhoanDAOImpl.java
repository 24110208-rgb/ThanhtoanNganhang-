package com.mycompany.thanhtoannganhang.model.dao;

import com.mycompany.thanhtoannganhang.model.entity.TaiKhoan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

/**
 * TANG MODEL - DAO Implementation
 * Dung RESOURCE_LOCAL EntityManager (tuong thich Tomcat).
 */
@ApplicationScoped
public class TaiKhoanDAOImpl implements TaiKhoanDAO {

    private static final EntityManagerFactory EMF =
        Persistence.createEntityManagerFactory("my_persistence_unit");

    private EntityManager getEM() {
        return EMF.createEntityManager();
    }

    @Override
    public TaiKhoan save(TaiKhoan taiKhoan) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(taiKhoan);
            em.getTransaction().commit();
            return taiKhoan;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<TaiKhoan> findById(Long id) {
        EntityManager em = getEM();
        try {
            return Optional.ofNullable(em.find(TaiKhoan.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<TaiKhoan> findBySoTaiKhoan(String soTaiKhoan) {
        EntityManager em = getEM();
        try {
            TaiKhoan result = em
                .createNamedQuery("TaiKhoan.findBySoTaiKhoan", TaiKhoan.class)
                .setParameter("soTaiKhoan", soTaiKhoan)
                .getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<TaiKhoan> findAll() {
        EntityManager em = getEM();
        try {
            return em.createNamedQuery("TaiKhoan.findAll", TaiKhoan.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public TaiKhoan update(TaiKhoan taiKhoan) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            TaiKhoan merged = em.merge(taiKhoan);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            TaiKhoan t = em.find(TaiKhoan.class, id);
            if (t != null) em.remove(t);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
