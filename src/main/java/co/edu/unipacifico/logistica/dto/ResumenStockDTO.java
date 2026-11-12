package co.edu.unipacifico.logistica.dto;

/** Stock total de un producto (todas las bodegas) comparado con su mínimo y su máximo. */
public class ResumenStockDTO {

    public static final String BAJO = "BAJO";
    public static final String NORMAL = "NORMAL";
    public static final String EXCESO = "EXCESO";

    private Long productoId;
    private String codigo;
    private String nombre;
    private String unidadMedida;
    private int total;
    private int stockMinimo;
    private int stockMaximo;

    public ResumenStockDTO(Long productoId, String codigo, String nombre, String unidadMedida,
                           int total, int stockMinimo, int stockMaximo) {
        this.productoId = productoId;
        this.codigo = codigo;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.total = total;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
    }

    /** BAJO si no alcanza el mínimo (hay que comprar); EXCESO si supera el máximo. */
    public String getNivel() {
        if (total < stockMinimo) return BAJO;
        if (stockMaximo > 0 && total > stockMaximo) return EXCESO;
        return NORMAL;
    }

    /** Unidades sugeridas para volver al stock máximo cuando el nivel es BAJO. */
    public int getSugeridoComprar() {
        return BAJO.equals(getNivel()) ? Math.max(stockMaximo - total, stockMinimo - total) : 0;
    }

    public Long getProductoId() { return productoId; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getUnidadMedida() { return unidadMedida; }
    public int getTotal() { return total; }
    public int getStockMinimo() { return stockMinimo; }
    public int getStockMaximo() { return stockMaximo; }
}
