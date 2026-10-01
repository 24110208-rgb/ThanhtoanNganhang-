package com.mycompany.thanhtoannganhang.vnpay;

/**
 * Cau hinh VNPay Sandbox.
 * Cac gia tri duoc doc tu System property (truyen qua -D khi chay)
 * hoac fallback ve gia tri mac dinh sandbox de tien test.
 */
public final class VNPayConfig {

    // Doc tu environment variable, fallback sandbox mac dinh
    public static final String TMN_CODE   =
        System.getProperty("VNP_TMN_CODE",   "UEIRHIDM");

    public static final String HASH_SECRET =
        System.getProperty("VNP_HASH_SECRET","FVVDUQVKCTCIIAMQYCEMKFVFAXLXHTCZ");

    public static final String PAY_URL =
        System.getProperty("VNP_URL",
            "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");

    public static final String RETURN_URL =
        System.getProperty("VNP_RETURN_URL",
            "https://thanhtoannganhang.onrender.com/api/vnpay/return");

    public static final String VERSION   = "2.1.0";
    public static final String COMMAND   = "pay";
    public static final String CURR_CODE = "VND";
    public static final String LOCALE    = "vn";
    public static final String ORDER_TYPE = "other";

    private VNPayConfig() {}
}
