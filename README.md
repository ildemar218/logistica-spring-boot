# Sistema de logística: inventario y cadena de suministro

Aplicación web con Spring Boot, Thymeleaf y MariaDB. Sigue la estructura de la guía
*Mi primer proyecto con Spring Boot* (partes 1, 2 y 3), aplicada a la logística en lugar
del mantenimiento de vehículos.

## Cómo ejecutarlo

1. **Base de datos.** En HeidiSQL, abrir y ejecutar completo `sql/01_esquema.sql`.
   Opcional: ejecutar después `sql/02_datos_prueba.sql` (categorías, bodegas, proveedores,
   clientes y productos de ejemplo).
2. **Credenciales.** Copiar `.env.example` como `.env` y escribir el usuario y la contraseña
   de MariaDB. El archivo `.env` no se entrega: `application.properties` solo lee las variables
   `DB_USER` y `DB_PASSWORD`.
3. **Arrancar.**
   - En VS Code: abrir la carpeta `logistica`, ir a *Ejecutar y depurar* y elegir **Logistica**
     (lee el archivo `.env`).
   - En una terminal, con `DB_USER` y `DB_PASSWORD` definidas: `.\mvnw.cmd spring-boot:run`
4. Abrir <http://localhost:8080>, registrarse e iniciar sesión.

## Recorrido de prueba

1. Crear una **orden de compra** a un proveedor, con destino a una bodega. Queda PENDIENTE.
2. Abrir la orden y pulsar **Registrar recepción en bodega**: el stock sube.
3. Revisar **Existencias** (stock por bodega y alertas de stock mínimo) y el **Kardex**.
4. Crear un **despacho** a un cliente desde esa bodega: el stock baja. Si se pide más de lo
   disponible, el sistema lo rechaza y no descuenta nada.
5. Marcar el despacho como **entregado**, o **anularlo** para devolver la mercancía a la bodega.

## Correspondencia con la guía

| Guía (mantenimiento de vehículos) | Este proyecto (logística) |
|---|---|
| Parte 1: entorno, MariaDB, `InicioController` e `inicio.html` | Igual; base `logistica` |
| Parte 2: registro, login, roles, BCrypt, `/principal` | Igual; `/principal` es un tablero de la cadena de suministro |
| Clientes | Clientes (destino de los despachos) |
| Mecánicos | Proveedores |
| Tipos de servicio | Categorías de producto |
| Productos | Productos (código, categoría, unidad, precios, stock mínimo y máximo) |
| Vehículos (relacionado con cliente) | Bodegas, y productos relacionados con su categoría |
| Factura (encabezado + detalle + IVA) | Órdenes de compra y despachos (encabezado + detalle + IVA 19 %) |
| — | Existencias por bodega y kardex de movimientos |

## Estructura

```
src/main/java/co/edu/unipacifico/logistica/
  LogisticaApplication.java
  config/       SecurityConfiguration (rutas públicas, login, BCrypt)
  controller/   un controlador por módulo + ControladorGlobal (404 y usuario del menú)
  dto/          objetos de los formularios, con sus validaciones
  exception/    RecursoNoEncontradoException (404) y ReglaNegocioException
  mapper/       conversión entidad <-> DTO (manual, como en la guía; MapStruct para usuarios)
  model/        entidades JPA, una por tabla
  repository/   interfaces JpaRepository
  service/      interfaces e implementaciones con las reglas de negocio
src/main/resources/
  templates/    vistas Thymeleaf por módulo (index, crear, editar o ver)
  static/       css/estilos.css y js/detalle.js (filas dinámicas del detalle)
sql/            01_esquema.sql y 02_datos_prueba.sql
```

## Reglas principales

- Todas las páginas, salvo inicio, registro y login, exigen sesión. Los formularios POST llevan token CSRF.
- Los documentos y códigos son únicos; la edición excluye el propio registro al comprobarlo.
- Un id inexistente responde con una página 404 controlada.
- Un registro con relaciones no se elimina: se informa y se puede marcar como Inactivo.
- Los importes (subtotal, IVA 19 % y total) se recalculan en el servidor con `BigDecimal`.
- El stock solo cambia en `InventarioService`, y cada cambio queda en el kardex con el usuario.
- Al descontar stock se bloquea la fila de existencias, para que dos despachos simultáneos
  no vendan más de lo que hay.
- Una orden solo se recibe o se anula si está PENDIENTE; un despacho solo se entrega o se
  anula si está en tránsito.
