package co.edu.unipacifico.logistica.dto;

import co.edu.unipacifico.logistica.config.Roles;
import java.util.Collection;

/**
 * Qué puede hacer el usuario con sesión. Las vistas lo usan para mostrar u ocultar menús y botones.
 * La protección real está en SecurityConfiguration y en los servicios; esto solo evita mostrar
 * opciones que el usuario no puede usar.
 */
public class Permisos {

    private final boolean admin;
    private final boolean coordinador;
    private final boolean auxiliar;
    private final boolean transportador;

    public Permisos(Collection<String> roles) {
        this.admin = roles.contains(Roles.ADMIN);
        this.coordinador = roles.contains(Roles.COORDINADOR);
        this.auxiliar = roles.contains(Roles.AUXILIAR_BODEGA);
        this.transportador = roles.contains(Roles.TRANSPORTADOR);
    }

    public boolean isAdmin() { return admin; }
    public boolean isCoordinador() { return coordinador; }
    public boolean isAuxiliar() { return auxiliar; }
    public boolean isTransportador() { return transportador; }

    /** Crear, editar y eliminar catálogos. */
    public boolean isEditarCatalogos() { return admin || coordinador; }
    /** Consultar catálogos y trabajar en bodega: entradas, salidas, transferencias, inventario y reportes. */
    public boolean isBodega() { return admin || coordinador || auxiliar; }
    /** Anular entradas y salidas. */
    public boolean isAnular() { return admin || coordinador; }
    /** Crear, asignar y cancelar despachos. */
    public boolean isGestionDespachos() { return admin || coordinador; }
    /** Poner un despacho en ruta (sale de la bodega). */
    public boolean isDespachar() { return admin || coordinador || auxiliar; }
    /** Registrar entrega o devolución. */
    public boolean isEntregar() { return admin || coordinador || transportador; }
    /** Ver el módulo de despachos. */
    public boolean isVerDespachos() { return isBodega() || transportador; }
    /** Solo ve sus propios despachos. */
    public boolean isSoloTransportador() { return transportador && !isBodega(); }
    public boolean isSinRol() { return !isBodega() && !transportador; }
}
