package com.mycompany.thanhtoannganhang.vnpay;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * TANG CONTROLLER - VNPay REST Resource
 *
 * POST /api/vnpay/create  → tao URL thanh toan, tra ve paymentUrl
 * GET  /api/vnpay/return  → VNPay callback sau khi thanh toan xong
 */
@Path("/vnpay")
@Produces(MediaType.APPLICATION_JSON)
public class VNPayResource {

    @Inject
    private VNPayService service;

    @Context
    private HttpServletRequest request;

    // ─────────────────────────────────────────────────────────────
    //  POST /api/vnpay/create
    //  Body: { soTaiKhoanNguon, soTien, noiDung }
    // ─────────────────────────────────────────────────────────────
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPayment(VNPayCreateRequest req) {
        try {
            String ip = getClientIp(request);
            VNPayService.VNPayCreateResponse res = service.createPaymentUrl(
                req.getSoTaiKhoanNguon(),
                req.getSoTien(),
                req.getNoiDung(),
                ip
            );
            return Response.ok(res).build();
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Loi tao URL: " + e.getMessage());
            return Response.status(500).entity(err).build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  GET /api/vnpay/return
    //  VNPay redirect nguoi dung ve day sau khi thanh toan
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/return")
    public Response handleReturn(@Context UriInfo uriInfo) {
        try {
            // Lay tat ca query params tu VNPay
            Map<String, String> params = new HashMap<>();
            uriInfo.getQueryParameters().forEach((k, v) -> {
                if (v != null && !v.isEmpty()) params.put(k, v.get(0));
            });

            VNPayService.VNPayReturnResponse res = service.handleReturn(params);

            // Redirect ve trang chu kem ket qua
            String status  = res.success ? "success" : "failed";
            String message = java.net.URLEncoder.encode(res.message,
                java.nio.charset.StandardCharsets.UTF_8);
            String txnRef  = res.txnRef != null ? res.txnRef : "";

            // Redirect ve frontend voi thong tin ket qua
            URI redirectUri = URI.create(
                "/?vnp_status=" + status
                + "&vnp_txnRef=" + txnRef
                + "&vnp_message=" + message
            );
            return Response.seeOther(redirectUri).build();

        } catch (Exception e) {
            return Response.seeOther(
                URI.create("/?vnp_status=failed&vnp_message=Loi+he+thong")
            ).build();
        }
    }

    // ─── Helper: lay IP thuc cua client ──────────────────────────
    private String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        // X-Forwarded-For co the chua nhieu IP, lay cai dau tien
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "127.0.0.1";
    }
}
