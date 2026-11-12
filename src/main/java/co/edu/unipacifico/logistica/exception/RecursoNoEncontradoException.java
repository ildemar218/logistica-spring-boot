package co.edu.unipacifico.logistica.exception;

/** Se lanza cuando se pide un registro por un id que no existe. Se responde con 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
