USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'GiaPha')
BEGIN
    ALTER DATABASE GiaPha SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE GiaPha;
END
GO

CREATE DATABASE GiaPha;
GO

USE GiaPha;
GO

SET QUOTED_IDENTIFIER ON;
GO

CREATE TABLE TaiKhoan (
    id_TaiKhoan         INT IDENTITY(1,1) PRIMARY KEY,
    TenHienThi          NVARCHAR(200) NOT NULL,
    SDT                 VARCHAR(15) NOT NULL UNIQUE,
    Email               VARCHAR(255) NULL,
    MatKhau             NVARCHAR(255) NULL,
    LoaiDangNhap        NVARCHAR(20) NOT NULL DEFAULT N'SDT_OTP'
                        CHECK (LoaiDangNhap IN (N'SDT_OTP', N'MatKhau', N'Google', N'Apple')),
    TrangThaiTaiKhoan   NVARCHAR(20) NOT NULL DEFAULT N'HoatDong'
                        CHECK (TrangThaiTaiKhoan IN (N'HoatDong', N'DaKhoa')),
    LoaiTaiKhoan        NVARCHAR(20) NOT NULL DEFAULT N'Thuong'
                        CHECK (LoaiTaiKhoan IN (N'Thuong', N'AdminApp')),
    NgayTao             DATETIME NOT NULL DEFAULT GETDATE(),
    LanDangNhapCuoi     DATETIME NULL
);
GO

CREATE TABLE MaXacThuc (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    SDT         VARCHAR(15) NOT NULL,
    Ma          VARCHAR(10) NOT NULL,
    NgayTao     DATETIME NOT NULL DEFAULT GETDATE(),
    HetHan      DATETIME NOT NULL,
    DaSuDung    BIT NOT NULL DEFAULT 0
);
GO

CREATE TABLE KhongGian (
    id_KhongGian     INT IDENTITY(1,1) PRIMARY KEY,
    TenKhongGian     NVARCHAR(200) NOT NULL,
    id_NguoiTao      INT NOT NULL,
    NgayTao          DATETIME NOT NULL DEFAULT GETDATE(),
    CheDoRiengTu     BIT NOT NULL DEFAULT 0,

    CONSTRAINT FK_KhongGian_NguoiTao
        FOREIGN KEY (id_NguoiTao) REFERENCES TaiKhoan(id_TaiKhoan)
);
GO

CREATE TABLE OThanhVien (
    id_OThanhVien      INT IDENTITY(1,1) PRIMARY KEY,
    id_KhongGian       INT NOT NULL,
    HoTen              NVARCHAR(200) NOT NULL,
    BietDanh           NVARCHAR(200) NULL,
    GioiTinh           NVARCHAR(10)  NULL
                        CHECK (GioiTinh IN (N'Nam', N'Nu', N'Khac')),
    NgaySinh           DATE NULL,
    QueQuan            NVARCHAR(300) NULL,
    ConSong            BIT NOT NULL DEFAULT 1,
    NgayMat            DATE NULL,
    CCCD               VARCHAR(20) NULL,
    TrangThaiLienKet   NVARCHAR(20) NOT NULL DEFAULT N'DangNoi'
                        CHECK (TrangThaiLienKet IN (N'DangNoi', N'DaTachHo')),
    LaTruongToc        BIT NOT NULL DEFAULT 0,

    CONSTRAINT FK_OThanhVien_KhongGian
        FOREIGN KEY (id_KhongGian) REFERENCES KhongGian(id_KhongGian)
);
GO

CREATE TABLE ThanVienTrongKGian (
    id_ThanVienTrongKGian  INT IDENTITY(1,1) PRIMARY KEY,
    id_KhongGian           INT NOT NULL,
    id_TaiKhoan            INT NOT NULL,
    VaiTro                 NVARCHAR(20) NOT NULL
                            CHECK (VaiTro IN (N'Admin', N'QuanLy', N'ThanhVien', N'ThanhVienHo', N'KhachTam')),
    id_OThanhVien           INT NULL,
    TrangThai               NVARCHAR(20) NOT NULL DEFAULT N'HoatDong'
                            CHECK (TrangThai IN (N'HoatDong', N'BiCam', N'DaRoi')),
    NgayThamGia             DATETIME NOT NULL DEFAULT GETDATE(),
    NgayCam                 DATETIME NULL,
    LyDoCam                 NVARCHAR(500) NULL,
    id_NguoiCam              INT NULL,

    CONSTRAINT FK_ThanVienTrongKGian_KhongGian
        FOREIGN KEY (id_KhongGian) REFERENCES KhongGian(id_KhongGian),
    CONSTRAINT FK_ThanVienTrongKGian_TaiKhoan
        FOREIGN KEY (id_TaiKhoan) REFERENCES TaiKhoan(id_TaiKhoan),
    CONSTRAINT FK_ThanVienTrongKGian_OThanhVien
        FOREIGN KEY (id_OThanhVien) REFERENCES OThanhVien(id_OThanhVien),
    CONSTRAINT FK_ThanVienTrongKGian_NguoiCam
        FOREIGN KEY (id_NguoiCam) REFERENCES TaiKhoan(id_TaiKhoan),

    CONSTRAINT CK_ThanVienTrongKGian_KhachTam_KhongCoO
        CHECK (VaiTro <> N'KhachTam' OR id_OThanhVien IS NULL),

    CONSTRAINT UQ_ThanVienTrongKGian_KhongGian_TaiKhoan
        UNIQUE (id_KhongGian, id_TaiKhoan)
);
GO

CREATE UNIQUE NONCLUSTERED INDEX UX_ThanVienTrongKGian_OThanhVien
    ON ThanVienTrongKGian (id_OThanhVien)
    WHERE id_OThanhVien IS NOT NULL;
GO

CREATE TABLE QuanHeChaMe (
    id_con              INT NOT NULL,
    id_PhuHuynh         INT NOT NULL,
    loaiQheVoiCon       NVARCHAR(10) NOT NULL
                        CHECK (loaiQheVoiCon IN (N'Ruot', N'Nuoi', N'Ke')),
    NgayXacLap          DATE NULL,
    HienThiLienKet      BIT NOT NULL DEFAULT 1,

    CONSTRAINT PK_QuanHeChaMe PRIMARY KEY (id_con, id_PhuHuynh),
    CONSTRAINT FK_QuanHeChaMe_Con
        FOREIGN KEY (id_con) REFERENCES OThanhVien(id_OThanhVien),
    CONSTRAINT FK_QuanHeChaMe_PhuHuynh
        FOREIGN KEY (id_PhuHuynh) REFERENCES OThanhVien(id_OThanhVien)
);
GO

CREATE TABLE QHeHonNhan (
    Id                  INT IDENTITY(1,1) PRIMARY KEY,
    Id_Chong            INT NOT NULL,
    Id_Vo                INT NOT NULL,
    NgayCoi             DATE NULL,
    NgayLyHon           DATE NULL,
    TrangThai           NVARCHAR(20) NOT NULL DEFAULT N'DangKetHon'
                        CHECK (TrangThai IN (N'DangKetHon', N'DaLyHon', N'Goa')),
    ThuTuHonNhan        INT NULL,
    HienThiLienKet      BIT NOT NULL DEFAULT 1,

    CONSTRAINT FK_QHeHonNhan_Chong
        FOREIGN KEY (Id_Chong) REFERENCES OThanhVien(id_OThanhVien),
    CONSTRAINT FK_QHeHonNhan_Vo
        FOREIGN KEY (Id_Vo) REFERENCES OThanhVien(id_OThanhVien)
);
GO

CREATE TABLE Avatar (
    id_Avatar      INT IDENTITY(1,1) PRIMARY KEY,
    id_OThanhVien  INT NOT NULL,
    DuongDanAnh    NVARCHAR(500) NULL,
    LoaiKhung      NVARCHAR(50) NULL,
    HieuUng        NVARCHAR(50) NULL,
    TrangThai      NVARCHAR(20) NOT NULL DEFAULT N'DangDung'
                   CHECK (TrangThai IN (N'DangDung', N'LichSu')),
    NgayTao        DATETIME NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_Avatar_OThanhVien
        FOREIGN KEY (id_OThanhVien) REFERENCES OThanhVien(id_OThanhVien)
);
GO

CREATE TABLE CuocDoi (
    id_CuocDoi     INT IDENTITY(1,1) PRIMARY KEY,
    id_OThanhVien  INT NOT NULL,
    id_NguoiViet   INT NOT NULL,
    NoiDung        NVARCHAR(MAX) NULL,
    NgayCapNhat    DATETIME NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_CuocDoi_OThanhVien
        FOREIGN KEY (id_OThanhVien) REFERENCES OThanhVien(id_OThanhVien),
    CONSTRAINT FK_CuocDoi_NguoiViet
        FOREIGN KEY (id_NguoiViet) REFERENCES TaiKhoan(id_TaiKhoan)
);
GO

CREATE TABLE BinhLuan (
    id_BinhLuan        INT IDENTITY(1,1) PRIMARY KEY,
    id_CuocDoi         INT NOT NULL,
    id_NguoiBinhLuan   INT NOT NULL,
    NoiDung            NVARCHAR(MAX) NOT NULL,
    NgayBinhLuan       DATETIME NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_BinhLuan_CuocDoi
        FOREIGN KEY (id_CuocDoi) REFERENCES CuocDoi(id_CuocDoi),
    CONSTRAINT FK_BinhLuan_NguoiBinhLuan
        FOREIGN KEY (id_NguoiBinhLuan) REFERENCES TaiKhoan(id_TaiKhoan)
);
GO

CREATE TABLE ThanhTuu (
    id_ThanhTuu    INT IDENTITY(1,1) PRIMARY KEY,
    id_OThanhVien  INT NOT NULL,
    TenThanhTuu    NVARCHAR(300) NOT NULL,
    MoTa           NVARCHAR(1000) NULL,
    Nam            INT NULL,

    CONSTRAINT FK_ThanhTuu_OThanhVien
        FOREIGN KEY (id_OThanhVien) REFERENCES OThanhVien(id_OThanhVien)
);
GO

CREATE TABLE MaCode (
    Ma             VARCHAR(50) PRIMARY KEY,
    id_KhongGian   INT NOT NULL,
    id_NguoiTao    INT NOT NULL,
    NgayTao        DATETIME NOT NULL DEFAULT GETDATE(),
    NgayHetHan     DATETIME NOT NULL,
    DaSuDung       BIT NOT NULL DEFAULT 0,
    id_NguoiDaDung INT NULL,
    NgaySuDung     DATETIME NULL,

    CONSTRAINT FK_MaCode_KhongGian
        FOREIGN KEY (id_KhongGian) REFERENCES KhongGian(id_KhongGian),
    CONSTRAINT FK_MaCode_NguoiTao
        FOREIGN KEY (id_NguoiTao) REFERENCES TaiKhoan(id_TaiKhoan),
    CONSTRAINT FK_MaCode_NguoiDaDung
        FOREIGN KEY (id_NguoiDaDung) REFERENCES TaiKhoan(id_TaiKhoan)
);
GO