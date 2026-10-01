package com.mycompany.thanhtoannganhang.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TẦNG CONTROLLER - DTO
 * Phản hồi trả về sau khi xử lý thanh toán.
 */
public class ThanhToanResponse {

    @JsonbProperty("success")
    private boolean success;

    @JsonbProperty("message")
    private String message;

    @JsonbProperty("maGiaoDich")
    private String maGiaoDich;

    @JsonbProperty("trangThai")
    private String trangThai;

    @JsonbProperty("soTien")
    private BigDecimal soTien;

    @JsonbProperty("thoiGian")
    private LocalDateTime thoiGian;

    /** URL / base64 ảnh QR (chỉ có khi loại = QR) */
    @JsonbProperty("qrImageUrl")
    private String qrImageUrl;

    // ─── Factory methods ──────────────────────────────────────────
    public static ThanhToanResponse ok(String maGiaoDich, BigDecimal soTien,
                                       String trangThai, String qrImageUrl) {
        ThanhToanResponse r = new ThanhToanResponse();
        r.success      = true;
        r.message      = "Giao dịch thành công";
        r.maGiaoDich   = maGiaoDich;
        r.soTien       = soTien;
        r.trangThai    = trangThai;
        r.thoiGian     = LocalDateTime.now();
        r.qrImageUrl   = qrImageUrl;
        return r;
    }

    public static ThanhToanResponse fail(String message) {
        ThanhToanResponse r = new ThanhToanResponse();
        r.success   = false;
        r.message   = message;
        r.trangThai = "FAILED";
        return r;
    }

    // ─── Getters & Setters ────────────────────────────────────────
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    public String getQrImageUrl() { return qrImageUrl; }
    public void setQrImageUrl(String qrImageUrl) { this.qrImageUrl = qrImageUrl; }
}
