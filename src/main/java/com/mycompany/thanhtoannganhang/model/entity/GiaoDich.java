package com.mycompany.thanhtoannganhang.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TẦNG MODEL - Entity
 * Lưu lịch sử mỗi giao dịch thanh toán (chuyển khoản hoặc QR).
 */
@Entity
@Table(name = "giao_dich")
@NamedQueries({
    @NamedQuery(name = "GiaoDich.findAll",
                query = "SELECT g FROM GiaoDich g ORDER BY g.thoiGian DESC"),
    @NamedQuery(name = "GiaoDich.findByTaiKhoanNguon",
                query = "SELECT g FROM GiaoDich g WHERE g.soTaiKhoanNguon = :stk ORDER BY g.thoiGian DESC"),
    @NamedQuery(name = "GiaoDich.findByMaGiaoDich",
                query = "SELECT g FROM GiaoDich g WHERE g.maGiaoDich = :ma")
})
public class GiaoDich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mã giao dịch duy nhất (UUID ngắn) */
    @Column(name = "ma_giao_dich", nullable = false, unique = true, length = 36)
    private String maGiaoDich;

    /** CHUYEN_KHOAN hoặc QR */
    @Column(name = "loai_giao_dich", nullable = false, length = 20)
    private String loaiGiaoDich;

    @Column(name = "so_tai_khoan_nguon", nullable = false, length = 20)
    private String soTaiKhoanNguon;

    @Column(name = "so_tai_khoan_dich", nullable = false, length = 20)
    private String soTaiKhoanDich;

    @Column(name = "ten_nguoi_nhan", length = 100)
    private String tenNguoiNhan;

    @Column(name = "ngan_hang_dich", length = 100)
    private String nganHangDich;

    @Column(name = "so_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal soTien;

    @Column(name = "noi_dung", length = 255)
    private String noiDung;

    /** PENDING / SUCCESS / FAILED */
    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai = "PENDING";

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    /** Dữ liệu QR (base64 hoặc URL) - chỉ dùng khi loại là QR */
    @Lob
    @Column(name = "qr_data")
    private String qrData;

    @PrePersist
    protected void onCreate() {
        thoiGian = LocalDateTime.now();
    }

    // ─── Constructors ───────────────────────────────────────────────
    public GiaoDich() {}

    // ─── Getters & Setters ──────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }

    public String getLoaiGiaoDich() { return loaiGiaoDich; }
    public void setLoaiGiaoDich(String loaiGiaoDich) { this.loaiGiaoDich = loaiGiaoDich; }

    public String getSoTaiKhoanNguon() { return soTaiKhoanNguon; }
    public void setSoTaiKhoanNguon(String soTaiKhoanNguon) { this.soTaiKhoanNguon = soTaiKhoanNguon; }

    public String getSoTaiKhoanDich() { return soTaiKhoanDich; }
    public void setSoTaiKhoanDich(String soTaiKhoanDich) { this.soTaiKhoanDich = soTaiKhoanDich; }

    public String getTenNguoiNhan() { return tenNguoiNhan; }
    public void setTenNguoiNhan(String tenNguoiNhan) { this.tenNguoiNhan = tenNguoiNhan; }

    public String getNganHangDich() { return nganHangDich; }
    public void setNganHangDich(String nganHangDich) { this.nganHangDich = nganHangDich; }

    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    public String getQrData() { return qrData; }
    public void setQrData(String qrData) { this.qrData = qrData; }
}
