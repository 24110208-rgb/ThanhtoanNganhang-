package com.mycompany.thanhtoannganhang.controller;

import com.mycompany.thanhtoannganhang.dto.ThanhToanRequest;
import com.mycompany.thanhtoannganhang.dto.ThanhToanResponse;
import com.mycompany.thanhtoannganhang.model.dao.GiaoDichDAOImpl;
import com.mycompany.thanhtoannganhang.model.dao.TaiKhoanDAOImpl;
import com.mycompany.thanhtoannganhang.model.entity.GiaoDich;
import com.mycompany.thanhtoannganhang.model.entity.TaiKhoan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * TẦNG CONTROLLER - Service
 * Chứa toàn bộ logic nghiệp vụ thanh toán.
 * Đứng giữa REST Resource (Controller) và DAO (Model).
 */
@ApplicationScoped
public class ThanhToanService {

    @Inject
    private TaiKhoanDAOImpl taiKhoanDAO;

    @Inject
    private GiaoDichDAOImpl giaoDichDAO;

    // ─────────────────────────────────────────────────────────────────
    //  CHUYỂN KHOẢN NGÂN HÀNG
    // ─────────────────────────────────────────────────────────────────
    public ThanhToanResponse chuyenKhoan(ThanhToanRequest req) {

        // 1. Kiểm tra tài khoản nguồn
        Optional<TaiKhoan> optNguon = taiKhoanDAO.findBySoTaiKhoan(req.getSoTaiKhoanNguon());
        if (optNguon.isEmpty()) {
            return ThanhToanResponse.fail("Tài khoản nguồn không tồn tại: " + req.getSoTaiKhoanNguon());
        }
        TaiKhoan nguon = optNguon.get();

        // 2. Kiểm tra trạng thái
        if (!"ACTIVE".equalsIgnoreCase(nguon.getTrangThai())) {
            return ThanhToanResponse.fail("Tài khoản nguồn đang bị khóa");
        }

        // 3. Kiểm tra số dư
        if (nguon.getSoDu().compareTo(req.getSoTien()) < 0) {
            return ThanhToanResponse.fail("Số dư không đủ. Hiện có: "
                    + formatTien(nguon.getSoDu()) + " VNĐ");
        }

        // 4. Trừ tiền tài khoản nguồn
        nguon.setSoDu(nguon.getSoDu().subtract(req.getSoTien()));
        taiKhoanDAO.update(nguon);

        // 5. Cộng tiền tài khoản đích (nếu trong hệ thống)
        taiKhoanDAO.findBySoTaiKhoan(req.getSoTaiKhoanDich()).ifPresent(dich -> {
            dich.setSoDu(dich.getSoDu().add(req.getSoTien()));
            taiKhoanDAO.update(dich);
        });

        // 6. Lưu giao dịch
        GiaoDich gd = buildGiaoDich(req, "CHUYEN_KHOAN", "SUCCESS", null);
        giaoDichDAO.save(gd);

        return ThanhToanResponse.ok(gd.getMaGiaoDich(), gd.getSoTien(), "SUCCESS", null);
    }

    // ─────────────────────────────────────────────────────────────────
    //  THANH TOÁN QR CODE  (tạo QR → URL VietQR công khai)
    // ─────────────────────────────────────────────────────────────────
    public ThanhToanResponse taoQR(ThanhToanRequest req) {

        // Tạo URL QR theo chuẩn VietQR (img.vietqr.io)
        // Format: https://img.vietqr.io/image/{bankId}-{accountNo}-{template}.png
        //         ?amount={amount}&addInfo={desc}&accountName={name}
        String bankId     = req.getNganHangDich() != null ? req.getNganHangDich() : "970415"; // Vietinbank mặc định
        String accountNo  = req.getSoTaiKhoanDich();
        String amount     = req.getSoTien().toPlainString();
        String addInfo    = req.getNoiDung() != null ? encodeUrl(req.getNoiDung()) : "Thanh+toan";

        String qrUrl = String.format(
            "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%s&addInfo=%s",
            bankId, accountNo, amount, addInfo
        );

        // Lưu giao dịch (PENDING — chờ xác nhận)
        GiaoDich gd = buildGiaoDich(req, "QR", "PENDING", qrUrl);
        giaoDichDAO.save(gd);

        return ThanhToanResponse.ok(gd.getMaGiaoDich(), gd.getSoTien(), "PENDING", qrUrl);
    }

    // ─────────────────────────────────────────────────────────────────
    //  XÁC NHẬN GIAO DỊCH QR
    // ─────────────────────────────────────────────────────────────────
    public ThanhToanResponse xacNhanQR(String maGiaoDich) {
        Optional<GiaoDich> opt = giaoDichDAO.findByMaGiaoDich(maGiaoDich);
        if (opt.isEmpty()) {
            return ThanhToanResponse.fail("Không tìm thấy giao dịch: " + maGiaoDich);
        }
        GiaoDich gd = opt.get();
        if (!"PENDING".equals(gd.getTrangThai())) {
            return ThanhToanResponse.fail("Giao dịch không ở trạng thái chờ xác nhận");
        }
        gd.setTrangThai("SUCCESS");
        giaoDichDAO.update(gd);
        return ThanhToanResponse.ok(gd.getMaGiaoDich(), gd.getSoTien(), "SUCCESS", null);
    }

    // ─────────────────────────────────────────────────────────────────
    //  LỊCH SỬ GIAO DỊCH
    // ─────────────────────────────────────────────────────────────────
    public List<GiaoDich> lichSuGiaoDich(String soTaiKhoan) {
        if (soTaiKhoan != null && !soTaiKhoan.isBlank()) {
            return giaoDichDAO.findByTaiKhoanNguon(soTaiKhoan);
        }
        return giaoDichDAO.findAll();
    }

    // ─────────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────────
    private GiaoDich buildGiaoDich(ThanhToanRequest req, String loai,
                                   String trangThai, String qrData) {
        GiaoDich gd = new GiaoDich();
        gd.setMaGiaoDich(UUID.randomUUID().toString());
        gd.setLoaiGiaoDich(loai);
        gd.setSoTaiKhoanNguon(req.getSoTaiKhoanNguon());
        gd.setSoTaiKhoanDich(req.getSoTaiKhoanDich());
        gd.setNganHangDich(req.getNganHangDich());
        gd.setSoTien(req.getSoTien());
        gd.setNoiDung(req.getNoiDung());
        gd.setTrangThai(trangThai);
        gd.setQrData(qrData);
        return gd;
    }

    private String formatTien(BigDecimal amount) {
        return String.format("%,.0f", amount);
    }

    private String encodeUrl(String text) {
        return text.trim().replace(" ", "+");
    }
}
