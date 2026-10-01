package com.mycompany.thanhtoannganhang.model.dao;

import com.mycompany.thanhtoannganhang.model.entity.TaiKhoan;
import java.util.List;
import java.util.Optional;

/**
 * TẦNG MODEL - DAO Interface
 * Định nghĩa các thao tác CRUD cho TaiKhoan.
 */
public interface TaiKhoanDAO {
    TaiKhoan save(TaiKhoan taiKhoan);
    Optional<TaiKhoan> findById(Long id);
    Optional<TaiKhoan> findBySoTaiKhoan(String soTaiKhoan);
    List<TaiKhoan> findAll();
    TaiKhoan update(TaiKhoan taiKhoan);
    void delete(Long id);
}
