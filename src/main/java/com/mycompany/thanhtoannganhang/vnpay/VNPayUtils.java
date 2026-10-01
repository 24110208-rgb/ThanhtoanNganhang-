package com.mycompany.thanhtoannganhang.vnpay;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Tien ich VNPay:
 *  - Tao URL thanh toan co chu ky HMAC-SHA512
 *  - Xac minh chu ky callback tu VNPay
 */
public final class VNPayUtils {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private VNPayUtils() {}

    // ─────────────────────────────────────────────────────────────
    //  Tao URL thanh toan VNPay
    // ─────────────────────────────────────────────────────────────
    public static String buildPaymentUrl(String txnRef, long amount,
                                         String orderInfo, String ipAddr) {
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version",    VNPayConfig.VERSION);
        params.put("vnp_Command",    VNPayConfig.COMMAND);
        params.put("vnp_TmnCode",    VNPayConfig.TMN_CODE);
        // VNPay yeu cau so tien * 100 (don vi: dong, nhan 100)
        params.put("vnp_Amount",     String.valueOf(amount * 100));
        params.put("vnp_CurrCode",   VNPayConfig.CURR_CODE);
        params.put("vnp_TxnRef",     txnRef);
        params.put("vnp_OrderInfo",  orderInfo);
        params.put("vnp_OrderType",  VNPayConfig.ORDER_TYPE);
        params.put("vnp_Locale",     VNPayConfig.LOCALE);
        params.put("vnp_ReturnUrl",  VNPayConfig.RETURN_URL);
        params.put("vnp_IpAddr",     ipAddr);
        params.put("vnp_CreateDate", LocalDateTime.now().format(FMT));
        // Het han sau 15 phut
        params.put("vnp_ExpireDate",
            LocalDateTime.now().plusMinutes(15).format(FMT));

        // Build query string de tao chu ky (chua encode value)
        StringBuilder hashData = new StringBuilder();
        StringBuilder query    = new StringBuilder();

        for (Map.Entry<String, String> e : params.entrySet()) {
            if (e.getValue() != null && !e.getValue().isEmpty()) {
                hashData.append(e.getKey()).append('=')
                        .append(e.getValue()).append('&');
                query.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                     .append('=')
                     .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                     .append('&');
            }
        }

        // Xoa dau & cuoi
        if (hashData.length() > 0) hashData.deleteCharAt(hashData.length() - 1);
        if (query.length()    > 0) query.deleteCharAt(query.length() - 1);

        String secureHash = hmacSHA512(VNPayConfig.HASH_SECRET, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return VNPayConfig.PAY_URL + "?" + query;
    }

    // ─────────────────────────────────────────────────────────────
    //  Xac minh chu ky callback tu VNPay
    // ─────────────────────────────────────────────────────────────
    public static boolean verifyCallback(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null) return false;

        // Loai bo vnp_SecureHash va vnp_SecureHashType truoc khi tinh lai
        Map<String, String> signParams = new TreeMap<>(params);
        signParams.remove("vnp_SecureHash");
        signParams.remove("vnp_SecureHashType");

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> e : signParams.entrySet()) {
            if (e.getValue() != null && !e.getValue().isEmpty()) {
                hashData.append(e.getKey()).append('=')
                        .append(e.getValue()).append('&');
            }
        }
        if (hashData.length() > 0) hashData.deleteCharAt(hashData.length() - 1);

        String computed = hmacSHA512(VNPayConfig.HASH_SECRET, hashData.toString());
        return computed.equalsIgnoreCase(receivedHash);
    }

    // ─────────────────────────────────────────────────────────────
    //  HMAC-SHA512
    // ─────────────────────────────────────────────────────────────
    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA512 error", e);
        }
    }
}
