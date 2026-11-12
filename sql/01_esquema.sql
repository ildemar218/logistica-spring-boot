-- Sistema de logística: inventario y cadena de suministro
-- Base de datos MariaDB. Ejecutar completo en HeidiSQL.
-- Crea la base, las tablas y los roles. No contiene usuarios.
-- Si la base ya existía con una versión anterior, primero: DROP DATABASE logistica;

CREATE DATABASE IF NOT EXISTS logistica
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE logistica;

-- =========================================================
-- Usuarios y roles
-- =========================================================
CREATE TABLE IF NOT EXISTS rol (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(60) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  login VARCHAR(50) NOT NULL UNIQUE,
  nombre VARCHAR(60),
  apellido VARCHAR(50),
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion DATETIME NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS usuarios_roles (
  usuarios_roles_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  rol_id BIGINT NOT NULL,
  UNIQUE (usuario_id, rol_id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  FOREIGN KEY (rol_id) REFERENCES rol(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ROLE_USER: usuario registrado que aún no tiene un rol asignado por el administrador.
INSERT IGNORE INTO rol (nombre) VALUES
  ('ROLE_ADMIN'),
  ('ROLE_COORDINADOR'),
  ('ROLE_AUXILIAR_BODEGA'),
  ('ROLE_TRANSPORTADOR'),
  ('ROLE_USER');

-- =========================================================
-- Catálogos
-- =========================================================
CREATE TABLE IF NOT EXISTS proveedores (
  proveedor_id BIGINT NOT NULL AUTO_INCREMENT,
  tipo_documento VARCHAR(20) NOT NULL,
  numero_documento VARCHAR(30) NOT NULL,
  razon_social VARCHAR(120) NOT NULL,
  contacto VARCHAR(100),
  telefono VARCHAR(30),
  correo VARCHAR(120),
  direccion VARCHAR(200),
  ciudad VARCHAR(80),
  estado BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion DATETIME,
  fecha_actualizacion DATETIME,
  PRIMARY KEY (proveedor_id),
  UNIQUE (numero_documento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS clientes (
  cliente_id BIGINT NOT NULL AUTO_INCREMENT,
  tipo_documento VARCHAR(20) NOT NULL,
  numero_documento VARCHAR(30) NOT NULL,
  nombres VARCHAR(120) NOT NULL,
  telefono VARCHAR(30),
  correo VARCHAR(120),
  direccion VARCHAR(200),
  ciudad VARCHAR(80),
  estado BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion DATETIME,
  fecha_actualizacion DATETIME,
  PRIMARY KEY (cliente_id),
  UNIQUE (numero_documento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS categorias (
  categoria_id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  descripcion VARCHAR(255),
  estado BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (categoria_id),
  UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bodegas (
  bodega_id BIGINT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(20) NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  ciudad VARCHAR(80) NOT NULL,
  direccion VARCHAR(200),
  responsable VARCHAR(100),
  telefono VARCHAR(30),
  estado BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (bodega_id),
  UNIQUE (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS productos (
  producto_id BIGINT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(30) NOT NULL,
  nombre VARCHAR(120) NOT NULL,
  descripcion VARCHAR(255),
  categoria_id BIGINT NOT NULL,
  unidad_medida VARCHAR(20) NOT NULL,
  precio_compra DECIMAL(14,2) NOT NULL,
  precio_venta DECIMAL(14,2) NOT NULL,
  stock_minimo INT NOT NULL DEFAULT 0,
  stock_maximo INT NOT NULL DEFAULT 0,
  estado BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion DATETIME,
  fecha_actualizacion DATETIME,
  PRIMARY KEY (producto_id),
  UNIQUE (codigo),
  FOREIGN KEY (categoria_id) REFERENCES categorias(categoria_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- Inventario: existencias por bodega y kardex de movimientos
-- =========================================================
CREATE TABLE IF NOT EXISTS existencias (
  existencia_id BIGINT NOT NULL AUTO_INCREMENT,
  producto_id BIGINT NOT NULL,
  bodega_id BIGINT NOT NULL,
  cantidad INT NOT NULL DEFAULT 0,
  fecha_actualizacion DATETIME,
  PRIMARY KEY (existencia_id),
  UNIQUE (producto_id, bodega_id),
  FOREIGN KEY (producto_id) REFERENCES productos(producto_id),
  FOREIGN KEY (bodega_id) REFERENCES bodegas(bodega_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- tipo: ENTRADA o SALIDA.
-- concepto: ENTRADA_PROVEEDOR, VENTA, MERMA, AJUSTE, CONSUMO_INTERNO, TRANSFERENCIA,
--           DESPACHO, DEVOLUCION o ANULACION.
CREATE TABLE IF NOT EXISTS movimientos_inventario (
  movimiento_id BIGINT NOT NULL AUTO_INCREMENT,
  fecha DATETIME NOT NULL,
  tipo VARCHAR(10) NOT NULL,
  concepto VARCHAR(20) NOT NULL,
  producto_id BIGINT NOT NULL,
  bodega_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  saldo INT NOT NULL,
  referencia VARCHAR(60) NOT NULL,
  usuario VARCHAR(50),
  PRIMARY KEY (movimiento_id),
  FOREIGN KEY (producto_id) REFERENCES productos(producto_id),
  FOREIGN KEY (bodega_id) REFERENCES bodegas(bodega_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- Entradas de mercancía por proveedor
-- =========================================================
CREATE TABLE IF NOT EXISTS entradas (
  entrada_id BIGINT NOT NULL AUTO_INCREMENT,
  numero VARCHAR(20) NOT NULL,
  fecha DATE NOT NULL,
  proveedor_id BIGINT NOT NULL,
  bodega_id BIGINT NOT NULL,
  documento_proveedor VARCHAR(40),
  estado VARCHAR(15) NOT NULL,
  observacion VARCHAR(255),
  subtotal DECIMAL(14,2) NOT NULL,
  iva DECIMAL(14,2) NOT NULL,
  total DECIMAL(14,2) NOT NULL,
  usuario VARCHAR(50),
  fecha_creacion DATETIME,
  PRIMARY KEY (entrada_id),
  UNIQUE (numero),
  FOREIGN KEY (proveedor_id) REFERENCES proveedores(proveedor_id),
  FOREIGN KEY (bodega_id) REFERENCES bodegas(bodega_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS detalle_entrada (
  detalle_id BIGINT NOT NULL AUTO_INCREMENT,
  entrada_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  costo_unitario DECIMAL(14,2) NOT NULL,
  subtotal DECIMAL(14,2) NOT NULL,
  PRIMARY KEY (detalle_id),
  FOREIGN KEY (entrada_id) REFERENCES entradas(entrada_id),
  FOREIGN KEY (producto_id) REFERENCES productos(producto_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- Salidas de mercancía (venta, merma, ajuste, consumo interno)
-- =========================================================
CREATE TABLE IF NOT EXISTS salidas (
  salida_id BIGINT NOT NULL AUTO_INCREMENT,
  numero VARCHAR(20) NOT NULL,
  fecha DATE NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  bodega_id BIGINT NOT NULL,
  cliente_id BIGINT NULL,
  motivo VARCHAR(255) NOT NULL,
  estado VARCHAR(15) NOT NULL,
  usuario VARCHAR(50),
  fecha_creacion DATETIME,
  PRIMARY KEY (salida_id),
  UNIQUE (numero),
  FOREIGN KEY (bodega_id) REFERENCES bodegas(bodega_id),
  FOREIGN KEY (cliente_id) REFERENCES clientes(cliente_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS detalle_salida (
  detalle_id BIGINT NOT NULL AUTO_INCREMENT,
  salida_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  PRIMARY KEY (detalle_id),
  FOREIGN KEY (salida_id) REFERENCES salidas(salida_id),
  FOREIGN KEY (producto_id) REFERENCES productos(producto_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- Transferencias entre bodegas
-- =========================================================
CREATE TABLE IF NOT EXISTS transferencias (
  transferencia_id BIGINT NOT NULL AUTO_INCREMENT,
  numero VARCHAR(20) NOT NULL,
  fecha DATE NOT NULL,
  bodega_origen_id BIGINT NOT NULL,
  bodega_destino_id BIGINT NOT NULL,
  responsable VARCHAR(100),
  observacion VARCHAR(255),
  usuario VARCHAR(50),
  fecha_creacion DATETIME,
  PRIMARY KEY (transferencia_id),
  UNIQUE (numero),
  FOREIGN KEY (bodega_origen_id) REFERENCES bodegas(bodega_id),
  FOREIGN KEY (bodega_destino_id) REFERENCES bodegas(bodega_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS detalle_transferencia (
  detalle_id BIGINT NOT NULL AUTO_INCREMENT,
  transferencia_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  PRIMARY KEY (detalle_id),
  FOREIGN KEY (transferencia_id) REFERENCES transferencias(transferencia_id),
  FOREIGN KEY (producto_id) REFERENCES productos(producto_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- Órdenes de despacho y seguimiento de entregas
-- Estados: PENDIENTE, EN_RUTA, ENTREGADO, DEVUELTO, CANCELADO
-- =========================================================
CREATE TABLE IF NOT EXISTS despachos (
  despacho_id BIGINT NOT NULL AUTO_INCREMENT,
  numero VARCHAR(20) NOT NULL,
  fecha DATE NOT NULL,
  cliente_id BIGINT NOT NULL,
  bodega_id BIGINT NOT NULL,
  direccion_entrega VARCHAR(200) NOT NULL,
  transportador_id BIGINT NOT NULL,
  vehiculo VARCHAR(20),
  numero_guia VARCHAR(40),
  estado VARCHAR(15) NOT NULL,
  observacion VARCHAR(255),
  motivo_devolucion VARCHAR(255),
  subtotal DECIMAL(14,2) NOT NULL,
  iva DECIMAL(14,2) NOT NULL,
  total DECIMAL(14,2) NOT NULL,
  fecha_creacion DATETIME,
  fecha_salida DATETIME,
  fecha_entrega DATETIME,
  PRIMARY KEY (despacho_id),
  UNIQUE (numero),
  FOREIGN KEY (cliente_id) REFERENCES clientes(cliente_id),
  FOREIGN KEY (bodega_id) REFERENCES bodegas(bodega_id),
  FOREIGN KEY (transportador_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS detalle_despacho (
  detalle_id BIGINT NOT NULL AUTO_INCREMENT,
  despacho_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(14,2) NOT NULL,
  subtotal DECIMAL(14,2) NOT NULL,
  PRIMARY KEY (detalle_id),
  FOREIGN KEY (despacho_id) REFERENCES despachos(despacho_id),
  FOREIGN KEY (producto_id) REFERENCES productos(producto_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS seguimiento_despacho (
  seguimiento_id BIGINT NOT NULL AUTO_INCREMENT,
  despacho_id BIGINT NOT NULL,
  fecha DATETIME NOT NULL,
  estado VARCHAR(15) NOT NULL,
  usuario VARCHAR(50),
  observacion VARCHAR(255),
  PRIMARY KEY (seguimiento_id),
  FOREIGN KEY (despacho_id) REFERENCES despachos(despacho_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Verificación
SHOW TABLES;
