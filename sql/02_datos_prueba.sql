-- Datos de prueba opcionales. Ejecutar después de 01_esquema.sql.
-- Las existencias empiezan en cero: el stock entra al recibir órdenes de compra.
USE logistica;

INSERT IGNORE INTO categorias (nombre, descripcion, estado) VALUES
  ('Alimentos', 'Productos alimenticios no perecederos', TRUE),
  ('Aseo', 'Productos de limpieza y aseo', TRUE),
  ('Papelería', 'Útiles de oficina y papelería', TRUE);

INSERT IGNORE INTO bodegas (codigo, nombre, ciudad, direccion, responsable, telefono, estado) VALUES
  ('BOD-BUN', 'Bodega Puerto', 'Buenaventura', 'Zona portuaria, calle 2 #5-10', 'Responsable Puerto', '6022400000', TRUE),
  ('BOD-CAL', 'Centro de distribución Cali', 'Cali', 'Autopista sur #30-45', 'Responsable Cali', '6023800000', TRUE);

INSERT IGNORE INTO proveedores (tipo_documento, numero_documento, razon_social, contacto, telefono, correo, direccion, ciudad, estado, fecha_creacion, fecha_actualizacion) VALUES
  ('NIT', '900100200-1', 'Distribuidora del Pacífico S.A.S.', 'Área comercial', '6022411111', 'ventas@pacifico.test', 'Calle 1 #2-3', 'Buenaventura', TRUE, NOW(), NOW()),
  ('NIT', '800300400-5', 'Mayorista Valle Ltda.', 'Área de pedidos', '6023822222', 'pedidos@mayorista.test', 'Carrera 10 #20-30', 'Cali', TRUE, NOW(), NOW());

INSERT IGNORE INTO clientes (tipo_documento, numero_documento, nombres, telefono, correo, direccion, ciudad, estado, fecha_creacion, fecha_actualizacion) VALUES
  ('NIT', '901500600-2', 'Supermercado La Bahía', '6022433333', 'compras@labahia.test', 'Barrio Centro, calle 5 #4-12', 'Buenaventura', TRUE, NOW(), NOW()),
  ('CC', '1112223334', 'Tienda Don Pedro', '3001234567', 'tiendadonpedro@correo.test', 'Barrio La Playita #8-20', 'Buenaventura', TRUE, NOW(), NOW());

INSERT IGNORE INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida, precio_compra, precio_venta, stock_minimo, stock_maximo, estado, fecha_creacion, fecha_actualizacion)
SELECT 'ARR-500', 'Arroz 500 g', 'Paquete de arroz blanco', categoria_id, 'UND', 1800.00, 2500.00, 50, 500, TRUE, NOW(), NOW() FROM categorias WHERE nombre = 'Alimentos';
INSERT IGNORE INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida, precio_compra, precio_venta, stock_minimo, stock_maximo, estado, fecha_creacion, fecha_actualizacion)
SELECT 'ACE-1L', 'Aceite 1 litro', 'Aceite vegetal', categoria_id, 'LT', 7500.00, 9800.00, 30, 300, TRUE, NOW(), NOW() FROM categorias WHERE nombre = 'Alimentos';
INSERT IGNORE INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida, precio_compra, precio_venta, stock_minimo, stock_maximo, estado, fecha_creacion, fecha_actualizacion)
SELECT 'DET-1K', 'Detergente 1 kg', 'Detergente en polvo', categoria_id, 'KG', 6200.00, 8500.00, 20, 200, TRUE, NOW(), NOW() FROM categorias WHERE nombre = 'Aseo';
INSERT IGNORE INTO productos (codigo, nombre, descripcion, categoria_id, unidad_medida, precio_compra, precio_venta, stock_minimo, stock_maximo, estado, fecha_creacion, fecha_actualizacion)
SELECT 'RES-CAR', 'Resma papel carta', 'Resma de 500 hojas', categoria_id, 'PAQ', 14000.00, 18500.00, 10, 100, TRUE, NOW(), NOW() FROM categorias WHERE nombre = 'Papelería';
