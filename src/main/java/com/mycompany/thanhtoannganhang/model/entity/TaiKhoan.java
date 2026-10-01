package com.mycompany.thanhtoannganhang.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TẦNG MODEL - Entity
 * Đại diện cho bảng TAI_KHOAN trong CSDL.
 */
@Entity
@Table(name = "tai_khoan")
@NamedQueries({
    @NamedQuery(name = "TaiKhoan.findBySoTaiKhoan",
                query = "SELECT t FROM TaiKhoan t WHERE t.soTaiKhoan = :soTaiKhoan"),
    @NamedQuery(name = "TaiKhoan.findAll",
                query = "SELECT t FROM TaiKhoan t ORDER BY t.id")
})
public class TaiKhoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "so_tai_khoan", nullable = false, unique = true, length = 20)
    private String soTaiKhoan;

    @Column(name = "chu_tai_khoan", nullable = false, length = 100)
    private String chuTaiKhoan;

    @Column(name = "ten_ngan_hang", nullable = false, length = 100)
    private String tenNganHang;

    @Column(name = "ma_ngan_hang", nullable = false, length = 10)
    private String maNganHang;  // vd: VCB, TCB, ACB...

    @Column(name = "so_du", nullable = false, precision = 18, scale = 2)
    private BigDecimal soDu = BigDecimal.ZERO;

    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai = "ACTIVE"; // ACTIVE / LOCKED

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }

    // ─── Constructors ───────────────────────────────────────────────
    public TaiKhoan() {}

    public TaiKhoan(String soTaiKhoan, String chuTaiKhoan,
                    String tenNganHang, String maNganHang) {
        this.soTaiKhoan  = soTaiKhoan;
        this.chuTaiKhoan = chuTaiKhoan;
        this.tenNganHang = tenNganHang;
        this.maNganHang  = maNganHang;
    }

    // ─── Getters & Setters ──────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSoTaiKhoan() { return soTaiKhoan; }
    public void setSoTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; }

    public String getChuTaiKhoan() { return chuTaiKhoan; }
    public void setChuTaiKhoan(String chuTaiKhoan) { this.chuTaiKhoan = chuTaiKhoan; }

    public String getTenNganHang() { return tenNganHang; }
    public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }

    public String getMaNganHang() { return maNganHang; }
    public void setMaNganHang(String maNganHang) { this.maNganHang = maNganHang; }

    public BigDecimal getSoDu() { return soDu; }
    public void setSoDu(BigDecimal soDu) { this.soDu = soDu; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}
