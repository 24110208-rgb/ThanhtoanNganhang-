package com.mycompany.thanhtoannganhang.model.dao;

import com.mycompany.thanhtoannganhang.model.entity.GiaoDich;
import java.util.List;
import java.util.Optional;

/**
 * TẦNG MODEL - DAO Interface cho GiaoDich.
 */
public interface GiaoDichDAO {
    GiaoDich save(GiaoDich giaoDich);
    Optional<GiaoDich> findById(Long id);
    Optional<GiaoDich> findByMaGiaoDich(String maGiaoDich);
    List<GiaoDich> findAll();
    List<GiaoDich> findByTaiKhoanNguon(String soTaiKhoan);
    GiaoDich update(GiaoDich giaoDich);
}
