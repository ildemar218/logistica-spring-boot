package co.edu.unipacifico.logistica.exception;

/**
 * Regla de negocio incumplida (documento duplicado, stock insuficiente, estado no válido...).
 * Si se indica el campo, el controlador muestra el mensaje junto a ese campo del formulario.
 */
public class ReglaNegocioException extends RuntimeException {

    private final String campo;

    public ReglaNegocioException(String mensaje) {
        this(null, mensaje);
    }

    public ReglaNegocioException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
