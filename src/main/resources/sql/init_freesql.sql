-- Chay tren phpMyAdmin freesqldatabase (khong can CREATE DATABASE / USE)

CREATE TABLE IF NOT EXISTS tai_khoan (
  id             BIGINT        NOT NULL AUTO_INCREMENT,
  so_tai_khoan   VARCHAR(20)   NOT NULL UNIQUE,
  chu_tai_khoan  VARCHAR(100)  NOT NULL,
  ten_ngan_hang  VARCHAR(100)  NOT NULL,
  ma_ngan_hang   VARCHAR(10)   NOT NULL,
  so_du          DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  trang_thai     VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
  ngay_tao       DATETIME,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS giao_dich (
  id                   BIGINT        NOT NULL AUTO_INCREMENT,
  ma_giao_dich         VARCHAR(36)   NOT NULL UNIQUE,
  loai_giao_dich       VARCHAR(20)   NOT NULL,
  so_tai_khoan_nguon   VARCHAR(20)   NOT NULL,
  so_tai_khoan_dich    VARCHAR(20)   NOT NULL,
  ten_nguoi_nhan       VARCHAR(100),
  ngan_hang_dich       VARCHAR(100),
  so_tien              DECIMAL(18,2) NOT NULL,
  noi_dung             VARCHAR(255),
  trang_thai           VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
  thoi_gian            DATETIME,
  qr_data              LONGTEXT,
  PRIMARY KEY (id),
  INDEX idx_tk_nguon   (so_tai_khoan_nguon),
  INDEX idx_trang_thai (trang_thai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO tai_khoan (so_tai_khoan, chu_tai_khoan, ten_ngan_hang, ma_ngan_hang, so_du, trang_thai, ngay_tao)
VALUES
  ('1234567890', 'Nguyen Van An',  'Vietcombank', 'VCB', 50000000.00, 'ACTIVE', NOW()),
  ('0987654321', 'Tran Thi Binh', 'Techcombank', 'TCB', 25000000.00, 'ACTIVE', NOW()),
  ('1122334455', 'Le Quang Cuong', 'MBBank',      'MBB', 10000000.00, 'ACTIVE', NOW()),
  ('5566778899', 'Pham Thu Dung',  'VPBank',      'VPB', 75000000.00, 'ACTIVE', NOW());
