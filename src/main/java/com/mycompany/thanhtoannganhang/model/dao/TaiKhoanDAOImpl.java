package com.mycompany.thanhtoannganhang.model.dao;

import com.mycompany.thanhtoannganhang.model.entity.TaiKhoan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * TẦNG MODEL - DAO Implementation
 * Dùng JPA EntityManager để truy cập CSDL.
 */
@ApplicationScoped
public class TaiKhoanDAOImpl implements TaiKhoanDAO {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Override
    @Transactional
    public TaiKhoan save(TaiKhoan taiKhoan) {
        em.persist(taiKhoan);
        return taiKhoan;
    }

    @Override
    public Optional<TaiKhoan> findById(Long id) {
        return Optional.ofNullable(em.find(TaiKhoan.class, id));
    }

    @Override
    public Optional<TaiKhoan> findBySoTaiKhoan(String soTaiKhoan) {
        try {
            TaiKhoan result = em.createNamedQuery("TaiKhoan.findBySoTaiKhoan", TaiKhoan.class)
                    .setParameter("soTaiKhoan", soTaiKhoan)
                    .getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<TaiKhoan> findAll() {
        return em.createNamedQuery("TaiKhoan.findAll", TaiKhoan.class).getResultList();
    }

    @Override
    @Transactional
    public TaiKhoan update(TaiKhoan taiKhoan) {
        return em.merge(taiKhoan);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(em::remove);
    }
}
