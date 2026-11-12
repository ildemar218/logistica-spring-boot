package co.edu.unipacifico.logistica.controller;

import co.edu.unipacifico.logistica.dto.ExistenciaDTO;
import co.edu.unipacifico.logistica.dto.MovimientoDTO;
import co.edu.unipacifico.logistica.model.Salida;
import co.edu.unipacifico.logistica.service.BodegaService;
import co.edu.unipacifico.logistica.service.InventarioService;
import co.edu.unipacifico.logistica.service.ProductoService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Reportes de movimientos e inventario disponible. Cada reporte se ve en pantalla, se puede
 * imprimir (o guardar como PDF desde el navegador) y exportar a CSV para abrirlo en Excel.
 */
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private static final List<String> CONCEPTOS;
    static {
        List<String> c = new ArrayList<>(List.of("ENTRADA_PROVEEDOR", "TRANSFERENCIA", "DESPACHO", "DEVOLUCION", "ANULACION"));
        c.addAll(Salida.TIPOS);
        CONCEPTOS = List.copyOf(c);
    }
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final InventarioService inventarioService;
    private final BodegaService bodegaService;
    private final ProductoService productoService;

    public ReporteController(InventarioService inventarioService, BodegaService bodegaService,
                             ProductoService productoService) {
        this.inventarioService = inventarioService;
        this.bodegaService = bodegaService;
        this.productoService = productoService;
    }

    @GetMapping({"", "/"})
    public String index() {
        return "reportes/index";
    }

    @GetMapping("/movimientos")
    public String movimientos(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                              @RequestParam(required = false) Long bodegaId,
                              @RequestParam(required = false) Long productoId,
                              @RequestParam(required = false) String tipo,
                              @RequestParam(required = false) String concepto,
                              Model model) {
        if (desde == null && hasta == null) {
            desde = LocalDate.now().withDayOfMonth(1);
            hasta = LocalDate.now();
        }
        List<MovimientoDTO> filas = inventarioService.reporteMovimientos(productoId, bodegaId, tipo, concepto, desde, hasta);
        model.addAttribute("movimientos", filas);
        model.addAttribute("totalEntradas", filas.stream().filter(m -> "ENTRADA".equals(m.getTipo())).mapToInt(MovimientoDTO::getCantidad).sum());
        model.addAttribute("totalSalidas", filas.stream().filter(m -> "SALIDA".equals(m.getTipo())).mapToInt(MovimientoDTO::getCantidad).sum());
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("bodegaId", bodegaId);
        model.addAttribute("productoId", productoId);
        model.addAttribute("tipo", tipo);
        model.addAttribute("concepto", concepto);
        model.addAttribute("conceptos", CONCEPTOS);
        model.addAttribute("bodegas", bodegaService.listar());
        model.addAttribute("productos", productoService.listar());
        return "reportes/movimientos";
    }

    @GetMapping("/movimientos.csv")
    public void movimientosCsv(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                               @RequestParam(required = false) Long bodegaId,
                               @RequestParam(required = false) Long productoId,
                               @RequestParam(required = false) String tipo,
                               @RequestParam(required = false) String concepto,
                               HttpServletResponse response) throws IOException {
        List<MovimientoDTO> filas = inventarioService.reporteMovimientos(productoId, bodegaId, tipo, concepto, desde, hasta);
        PrintWriter out = abrirCsv(response, "movimientos.csv");
        out.println("Fecha;Tipo;Concepto;Código;Producto;Bodega;Cantidad;Saldo;Referencia;Usuario");
        for (MovimientoDTO m : filas) {
            out.println(String.join(";", m.getFecha().format(FECHA_HORA), m.getTipo(), m.getConcepto(),
                    csv(m.getProductoCodigo()), csv(m.getProductoNombre()), csv(m.getBodegaNombre()),
                    String.valueOf(m.getCantidad()), String.valueOf(m.getSaldo()), csv(m.getReferencia()), csv(m.getUsuario())));
        }
        out.flush();
    }

    @GetMapping("/inventario")
    public String inventario(@RequestParam(required = false) Long bodegaId,
                             @RequestParam(required = false) Long productoId, Model model) {
        List<ExistenciaDTO> filas = inventarioService.existencias(bodegaId, productoId);
        model.addAttribute("existencias", filas);
        model.addAttribute("valorTotal", filas.stream().map(ExistenciaDTO::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("unidadesTotal", filas.stream().mapToInt(ExistenciaDTO::getCantidad).sum());
        model.addAttribute("alertas", inventarioService.alertasStockMinimo());
        model.addAttribute("bodegaId", bodegaId);
        model.addAttribute("productoId", productoId);
        model.addAttribute("bodegas", bodegaService.listar());
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("generado", LocalDate.now());
        return "reportes/inventario";
    }

    @GetMapping("/inventario.csv")
    public void inventarioCsv(@RequestParam(required = false) Long bodegaId,
                              @RequestParam(required = false) Long productoId,
                              HttpServletResponse response) throws IOException {
        PrintWriter out = abrirCsv(response, "inventario.csv");
        out.println("Bodega;Código;Producto;Unidad;Cantidad;Stock mínimo;Costo unitario;Valor total");
        for (ExistenciaDTO e : inventarioService.existencias(bodegaId, productoId)) {
            out.println(String.join(";", csv(e.getBodegaNombre()), csv(e.getProductoCodigo()), csv(e.getProductoNombre()),
                    e.getUnidadMedida(), String.valueOf(e.getCantidad()), String.valueOf(e.getStockMinimo()),
                    numero(e.getCostoUnitario()), numero(e.getValorTotal())));
        }
        out.flush();
    }

    /** CSV con BOM UTF-8 y punto y coma: Excel en español lo abre con columnas y tildes correctas. */
    private PrintWriter abrirCsv(HttpServletResponse response, String nombre) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + "\"");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter out = response.getWriter();
        out.write('﻿');
        return out;
    }

    private static String csv(String valor) {
        if (valor == null) return "";
        String v = valor.replace("\"", "\"\"");
        return v.contains(";") || v.contains("\"") || v.contains("\n") ? "\"" + v + "\"" : v;
    }

    private static String numero(BigDecimal valor) {
        return valor == null ? "" : valor.toPlainString().replace('.', ',');
    }
}
