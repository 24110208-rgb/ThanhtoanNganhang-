package com.mycompany.thanhtoannganhang.model.dao;

import com.mycompany.thanhtoannganhang.model.entity.GiaoDich;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * TẦNG MODEL - DAO Implementation cho GiaoDich.
 */
@ApplicationScoped
public class GiaoDichDAOImpl implements GiaoDichDAO {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Override
    @Transactional
    public GiaoDich save(GiaoDich giaoDich) {
        em.persist(giaoDich);
        return giaoDich;
    }

    @Override
    public Optional<GiaoDich> findById(Long id) {
        return Optional.ofNullable(em.find(GiaoDich.class, id));
    }

    @Override
    public Optional<GiaoDich> findByMaGiaoDich(String maGiaoDich) {
        try {
            GiaoDich g = em.createNamedQuery("GiaoDich.findByMaGiaoDich", GiaoDich.class)
                    .setParameter("ma", maGiaoDich)
                    .getSingleResult();
            return Optional.of(g);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<GiaoDich> findAll() {
        return em.createNamedQuery("GiaoDich.findAll", GiaoDich.class).getResultList();
    }

    @Override
    public List<GiaoDich> findByTaiKhoanNguon(String soTaiKhoan) {
        return em.createNamedQuery("GiaoDich.findByTaiKhoanNguon", GiaoDich.class)
                .setParameter("stk", soTaiKhoan)
                .getResultList();
    }

    @Override
    @Transactional
    public GiaoDich update(GiaoDich giaoDich) {
        return em.merge(giaoDich);
    }
}
