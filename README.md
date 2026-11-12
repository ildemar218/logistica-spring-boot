# Sistema de logística: inventario y cadena de suministro

Aplicación web con Spring Boot, Thymeleaf, Spring Security y MariaDB. Sigue la estructura de la
guía *Mi primer proyecto con Spring Boot* (partes 1, 2 y 3) y cumple el enunciado
**9 – Logística, inventario y cadena de suministro**.

## Cómo ejecutarlo

1. **Base de datos.** En HeidiSQL, ejecutar completo `sql/01_esquema.sql` y luego
   `sql/02_datos_prueba.sql` (productos, bodegas, proveedores, clientes y un usuario por rol).
   Si ya tenía una versión anterior de la base: `DROP DATABASE logistica;` antes.
2. **Credenciales.** Copiar `.env.example` como `.env` y escribir el usuario y la contraseña
   de MariaDB. El archivo `.env` no se entrega.
3. **Arrancar** (desde la carpeta `logistica`):
   ```
   powershell -ExecutionPolicy Bypass -File .\iniciar.ps1
   ```
   También sirve *Run As > Spring Boot App* en Spring Tools, con `DB_USER` y `DB_PASSWORD`
   en *Run Configurations > Environment*, como en la guía.
4. Abrir <http://localhost:8080> e iniciar sesión.

### Usuarios de demostración (contraseña de todos: `Demo2026*`)

| Login | Rol |
|---|---|
| `admin` | Administrador |
| `coordinador` | Coordinador logístico |
| `auxiliar` | Auxiliar de bodega |
| `transportador` | Transportador |

Son solo para pruebas locales. Si no se cargan los datos de prueba, el **primer usuario que se
registre** queda como Administrador; los siguientes quedan sin rol hasta que el administrador
les asigne uno en **Usuarios**.

## Requisitos del enunciado y dónde se cumplen

| Requisito | Módulo |
|---|---|
| Registrar productos, proveedores y bodegas | Productos, Proveedores, Bodegas (y Clientes, Categorías) |
| Registrar entradas de mercancía por proveedor | **Entradas**: proveedor, bodega, factura o remisión, productos y costos; suma el stock |
| Registrar salidas por venta o despacho | **Salidas** (venta, merma, ajuste, consumo interno) y **Despachos** |
| Transferencias entre bodegas | **Transferencias**: sale del origen y entra al destino en una sola transacción |
| Consultar inventario por producto y bodega | **Inventario**: filtros por bodega y producto, nivel frente a mínimo y máximo |
| Generar órdenes de despacho | **Despachos** |
| Asignar responsable o transportador | Cada despacho tiene un usuario Transportador y un vehículo; se puede reasignar mientras está pendiente |
| Estados pendiente, en ruta, entregado, devuelto y cancelado | **Despachos**, con historial de seguimiento (fecha, estado, usuario, detalle) |
| Alertas de stock mínimo | Tablero, Inventario y reporte de inventario |
| Reportes de movimientos e inventario disponible | **Reportes**: filtros por fechas, bodega, producto, tipo y concepto; exportar a Excel (CSV) e imprimir o guardar como PDF |

### Estados del despacho

```
PENDIENTE ──despachar──▶ EN_RUTA ──entregar──▶ ENTREGADO
    │                       └──devolver──▶ DEVUELTO   (la mercancía vuelve a la bodega)
    └──cancelar──▶ CANCELADO                          (no se movió mercancía)
```
El stock se descuenta al pasar a EN_RUTA. Al crear la orden se comprueba que haya existencias.

### Roles

| Rol | Puede |
|---|---|
| Administrador | Todo, incluida la gestión de usuarios y roles |
| Coordinador logístico | Catálogos, entradas, salidas, transferencias, despachos (crear, asignar, cancelar), inventario y reportes; anular entradas y salidas |
| Auxiliar de bodega | Consultar catálogos; registrar entradas, salidas y transferencias; despachar (pasar a EN RUTA); inventario y reportes |
| Transportador | Solo sus despachos asignados: verlos y registrar la entrega o la devolución |

Los permisos se aplican en `SecurityConfiguration` (por ruta) y en los servicios (por ejemplo,
un transportador no puede actuar sobre despachos de otro). El menú solo muestra lo permitido.

## Escenario de aceptación

1. Iniciar sesión como `auxiliar` y registrar una **entrada** de 100 unidades de Arroz en Bodega Puerto.
2. Registrar una **transferencia** de 30 unidades de Bodega Puerto a Bodega Tumaco. La página de
   la transferencia muestra las existencias resultantes: 70 en origen y 30 en destino.
3. Como `coordinador`, crear un **despacho** desde Bodega Puerto, asignado a `transportador`.
   Como `auxiliar`, pulsar **Despachar**; como `transportador`, **Marcar como entregado**.
4. Verificar **Inventario** (existencias por bodega), **Kardex** o **Reportes** (movimientos) y las
   **alertas de stock mínimo** en el tablero.
5. Intentar una **salida** de un producto sin existencias: el sistema la rechaza indicando lo
   disponible y lo solicitado, y no descuenta nada.

## Estructura

```
src/main/java/co/edu/unipacifico/logistica/
  config/       SecurityConfiguration (permisos por rol), Roles
  controller/   un controlador por módulo + ControladorGlobal (404 y permisos para las vistas)
  dto/          objetos de los formularios con sus validaciones; Permisos
  exception/    RecursoNoEncontradoException (404) y ReglaNegocioException
  mapper/       conversión entidad -> DTO (manual como en la guía; MapStruct para usuarios)
  model/        entidades JPA, una por tabla
  repository/   interfaces JpaRepository
  service/      reglas de negocio; InventarioService es el único que cambia el stock
src/main/resources/
  templates/    vistas Thymeleaf por módulo
  static/       css/estilos.css y js/detalle.js (filas dinámicas de los detalles)
sql/            01_esquema.sql y 02_datos_prueba.sql
```

## Reglas principales

- Todas las páginas, salvo inicio, registro y login, exigen sesión. Los formularios POST llevan token CSRF.
- Ninguna operación deja el stock negativo: si un producto no alcanza, se rechaza el documento completo.
- Cada cambio de stock queda en el kardex con tipo (entrada o salida), concepto, saldo, referencia y usuario.
- Al descontar stock se bloquea la fila de existencias para que dos operaciones simultáneas no saquen más de lo que hay.
- Los importes (subtotal, IVA 19 % y total) se recalculan en el servidor con `BigDecimal`.
- Documentos y códigos únicos; un id inexistente responde con una página 404 controlada.
- Un registro con relaciones no se elimina: se informa y se puede marcar como Inactivo.
