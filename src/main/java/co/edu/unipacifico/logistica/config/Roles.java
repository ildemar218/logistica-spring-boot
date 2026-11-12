package co.edu.unipacifico.logistica.config;

/** Nombres de los roles tal como están en la tabla rol. */
public final class Roles {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String COORDINADOR = "ROLE_COORDINADOR";
    public static final String AUXILIAR_BODEGA = "ROLE_AUXILIAR_BODEGA";
    public static final String TRANSPORTADOR = "ROLE_TRANSPORTADOR";
    /** Usuario registrado que todavía no tiene un rol asignado por el administrador. */
    public static final String SIN_ROL = "ROLE_USER";

    private Roles() {
    }

    /** Nombre legible para las vistas. */
    public static String etiqueta(String rol) {
        return switch (rol) {
            case ADMIN -> "Administrador";
            case COORDINADOR -> "Coordinador logístico";
            case AUXILIAR_BODEGA -> "Auxiliar de bodega";
            case TRANSPORTADOR -> "Transportador";
            case SIN_ROL -> "Sin rol asignado";
            default -> rol;
        };
    }
}
