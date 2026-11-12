package co.edu.unipacifico.logistica.exception;

public class RegistroDuplicadoException extends RuntimeException {

    private final String campo;

    public RegistroDuplicadoException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}