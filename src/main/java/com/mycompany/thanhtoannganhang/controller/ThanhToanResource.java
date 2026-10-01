package com.mycompany.thanhtoannganhang.controller;

import com.mycompany.thanhtoannganhang.dto.ThanhToanRequest;
import com.mycompany.thanhtoannganhang.dto.ThanhToanResponse;
import com.mycompany.thanhtoannganhang.model.entity.GiaoDich;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * TẦNG CONTROLLER - REST Resource
 * Nhận HTTP request từ View, gọi Service xử lý, trả JSON về View.
 *
 * Base path: /api/thanhtoan
 */
@Path("/thanhtoan")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ThanhToanResource {

    private final ThanhToanService service = new ThanhToanService();

    // ─────────────────────────────────────────────────────────────────
    //  POST /api/thanhtoan/chuyenkhoan
    //  Thực hiện chuyển khoản ngân hàng
    // ─────────────────────────────────────────────────────────────────
    @POST
    @Path("/chuyenkhoan")
    public Response chuyenKhoan(@Valid ThanhToanRequest req) {
        try {
            req.setLoaiGiaoDich("CHUYEN_KHOAN");
            ThanhToanResponse res = service.chuyenKhoan(req);
            int status = res.isSuccess() ? 200 : 400;
            return Response.status(status).entity(res).build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity(ThanhToanResponse.fail("Lỗi hệ thống: " + e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  POST /api/thanhtoan/qr
    //  Tạo mã QR thanh toán (VietQR)
    // ─────────────────────────────────────────────────────────────────
    @POST
    @Path("/qr")
    public Response taoQR(@Valid ThanhToanRequest req) {
        try {
            req.setLoaiGiaoDich("QR");
            ThanhToanResponse res = service.taoQR(req);
            int status = res.isSuccess() ? 200 : 400;
            return Response.status(status).entity(res).build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity(ThanhToanResponse.fail("Lỗi hệ thống: " + e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  PUT /api/thanhtoan/qr/xacnhan/{maGiaoDich}
    //  Xác nhận QR đã được quét & thanh toán thành công
    // ─────────────────────────────────────────────────────────────────
    @PUT
    @Path("/qr/xacnhan/{maGiaoDich}")
    public Response xacNhanQR(@PathParam("maGiaoDich") String maGiaoDich) {
        try {
            ThanhToanResponse res = service.xacNhanQR(maGiaoDich);
            int status = res.isSuccess() ? 200 : 400;
            return Response.status(status).entity(res).build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity(ThanhToanResponse.fail("Lỗi hệ thống: " + e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  GET /api/thanhtoan/lichsu?stk=0123456789
    //  Lấy lịch sử giao dịch
    // ─────────────────────────────────────────────────────────────────
    @GET
    @Path("/lichsu")
    public Response lichSu(@QueryParam("stk") String soTaiKhoan) {
        try {
            List<GiaoDich> list = service.lichSuGiaoDich(soTaiKhoan);
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity(ThanhToanResponse.fail("Lỗi hệ thống: " + e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  GET /api/thanhtoan/ping  — kiểm tra server
    // ─────────────────────────────────────────────────────────────────
    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "ThanhToanNganhang API OK";
    }
}
