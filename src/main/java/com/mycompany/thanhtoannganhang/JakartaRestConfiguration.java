package com.mycompany.thanhtoannganhang;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configures Jakarta RESTful Web Services for the application.
 * @author Juneau
 */
/**
 * Đăng ký base path cho toàn bộ REST API.
 * Tất cả endpoint có dạng: /ThanhtoanNganhang/api/...
 */
@ApplicationPath("api")
public class JakartaRestConfiguration extends Application {

}
