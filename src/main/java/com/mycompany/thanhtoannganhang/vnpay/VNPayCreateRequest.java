package com.mycompany.thanhtoannganhang.vnpay;

import java.math.BigDecimal;

/**
 * DTO nhan request tao thanh toan VNPay tu View.
 */
public class VNPayCreateRequest {

    private String     soTaiKhoanNguon;
    private BigDecimal soTien;
    private String     noiDung;

    public String getSoTaiKhoanNguon() { return soTaiKhoanNguon; }
    public void setSoTaiKhoanNguon(String s) { this.soTaiKhoanNguon = s; }

    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal s) { this.soTien = s; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String n) { this.noiDung = n; }
}
