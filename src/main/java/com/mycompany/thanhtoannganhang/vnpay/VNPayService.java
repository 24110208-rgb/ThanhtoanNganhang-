package com.mycompany.thanhtoannganhang.vnpay;

import com.mycompany.thanhtoannganhang.model.dao.GiaoDichDAOImpl;
import com.mycompany.thanhtoannganhang.model.entity.GiaoDich;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * TANG CONTROLLER - VNPay Service
 * Xu ly logic tao URL thanh toan va xu ly ket qua callback.
 */
@ApplicationScoped
public class VNPayService {

    // Khoi tao truc tiep (Tomcat khong co full CDI de @Inject)
    private final GiaoDichDAOImpl giaoDichDAO = new GiaoDichDAOImpl();

    // ─────────────────────────────────────────────────────────────
    //  Tao URL thanh toan VNPay
    //  amount: so tien (VND), soTaiKhoanNguon: TK nguoi dung
    // ─────────────────────────────────────────────────────────────
    public VNPayCreateResponse createPaymentUrl(
            String soTaiKhoanNguon, BigDecimal amount,
            String noiDung, String ipAddr) {

        // Tao ma giao dich: timestamp + 4 ky tu cuoi TK
        String txnRef = System.currentTimeMillis() + ""
            + soTaiKhoanNguon.substring(Math.max(0, soTaiKhoanNguon.length() - 4));

        String orderInfo = (noiDung != null && !noiDung.isBlank())
            ? noiDung : "Thanh toan don hang " + txnRef;

        // Luu giao dich trang thai PENDING
        GiaoDich gd = new GiaoDich();
        gd.setMaGiaoDich(txnRef);
        gd.setLoaiGiaoDich("VNPAY");
        gd.setSoTaiKhoanNguon(soTaiKhoanNguon);
        gd.setSoTaiKhoanDich("VNPAY");
        gd.setNganHangDich("VNPay Sandbox");
        gd.setSoTien(amount);
        gd.setNoiDung(orderInfo);
        gd.setTrangThai("PENDING");
        giaoDichDAO.save(gd);

        // Tao URL chuyen huong
        String url = VNPayUtils.buildPaymentUrl(
            txnRef, amount.longValue(), orderInfo, ipAddr);

        return new VNPayCreateResponse(true, txnRef, url);
    }

    // ─────────────────────────────────────────────────────────────
    //  Xu ly ket qua callback tu VNPay (Return URL)
    // ─────────────────────────────────────────────────────────────
    public VNPayReturnResponse handleReturn(Map<String, String> params) {

        // 1. Xac minh chu ky
        if (!VNPayUtils.verifyCallback(params)) {
            return VNPayReturnResponse.fail("Chu ky khong hop le");
        }

        String txnRef    = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transNo   = params.get("vnp_TransactionNo");
        String amount    = params.get("vnp_Amount");   // da nhan 100
        String bankCode  = params.get("vnp_BankCode");

        // 2. Cap nhat trang thai trong DB
        Optional<GiaoDich> opt = giaoDichDAO.findByMaGiaoDich(txnRef);
        if (opt.isPresent()) {
            GiaoDich gd = opt.get();
            if ("00".equals(responseCode)) {
                gd.setTrangThai("SUCCESS");
            } else {
                gd.setTrangThai("FAILED");
            }
            giaoDichDAO.update(gd);
        }

        // 3. Tra ket qua
        boolean success = "00".equals(responseCode);
        String msg = success
            ? "Thanh toan VNPay thanh cong! Ma GD: " + transNo
            : "Thanh toan that bai. Ma loi: " + responseCode;

        return new VNPayReturnResponse(
            success, msg, txnRef, transNo,
            amount != null ? new BigDecimal(amount).divide(BigDecimal.valueOf(100)) : null,
            bankCode, responseCode
        );
    }

    // ─── Inner DTOs ───────────────────────────────────────────────
    public static class VNPayCreateResponse {
        public boolean success;
        public String  txnRef;
        public String  paymentUrl;
        public VNPayCreateResponse(boolean s, String t, String u) {
            success = s; txnRef = t; paymentUrl = u;
        }
    }

    public static class VNPayReturnResponse {
        public boolean    success;
        public String     message;
        public String     txnRef;
        public String     transactionNo;
        public BigDecimal amount;
        public String     bankCode;
        public String     responseCode;

        public VNPayReturnResponse(boolean s, String m, String t,
                                   String tn, BigDecimal a, String b, String rc) {
            success = s; message = m; txnRef = t;
            transactionNo = tn; amount = a; bankCode = b; responseCode = rc;
        }
        public static VNPayReturnResponse fail(String msg) {
            return new VNPayReturnResponse(false, msg, null, null, null, null, null);
        }
    }
}
