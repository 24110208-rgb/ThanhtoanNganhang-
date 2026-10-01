package com.mycompany.thanhtoannganhang.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * TẦNG CONTROLLER - DTO (Data Transfer Object)
 * Nhận dữ liệu từ View gửi lên khi thực hiện thanh toán.
 */
public class ThanhToanRequest {

    /** CHUYEN_KHOAN hoặc QR */
    @NotBlank(message = "Loại giao dịch không được trống")
    @JsonbProperty("loaiGiaoDich")
    private String loaiGiaoDich;

    @NotBlank(message = "Số tài khoản nguồn không được trống")
    @JsonbProperty("soTaiKhoanNguon")
    private String soTaiKhoanNguon;

    @NotBlank(message = "Số tài khoản đích không được trống")
    @JsonbProperty("soTaiKhoanDich")
    private String soTaiKhoanDich;

    @JsonbProperty("nganHangDich")
    private String nganHangDich;

    @NotNull(message = "Số tiền không được trống")
    @DecimalMin(value = "1000", message = "Số tiền tối thiểu 1.000 VNĐ")
    @JsonbProperty("soTien")
    private BigDecimal soTien;

    @Size(max = 255, message = "Nội dung không quá 255 ký tự")
    @JsonbProperty("noiDung")
    private String noiDung;

    // ─── Getters & Setters ─────────────────────────────────────────
    public String getLoaiGiaoDich() { return loaiGiaoDich; }
    public void setLoaiGiaoDich(String loaiGiaoDich) { this.loaiGiaoDich = loaiGiaoDich; }

    public String getSoTaiKhoanNguon() { return soTaiKhoanNguon; }
    public void setSoTaiKhoanNguon(String soTaiKhoanNguon) { this.soTaiKhoanNguon = soTaiKhoanNguon; }

    public String getSoTaiKhoanDich() { return soTaiKhoanDich; }
    public void setSoTaiKhoanDich(String soTaiKhoanDich) { this.soTaiKhoanDich = soTaiKhoanDich; }

    public String getNganHangDich() { return nganHangDich; }
    public void setNganHangDich(String nganHangDich) { this.nganHangDich = nganHangDich; }

    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
}
