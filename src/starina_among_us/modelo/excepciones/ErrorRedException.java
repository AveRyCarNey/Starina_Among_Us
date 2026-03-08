package starina_among_us.modelo.excepciones;

/**
 * Excepcion personalizada que se lanza cuando ocurre un problema critico
 * relacionado con la conexion multijugador (cliente-servidor).
 * Se utiliza en situaciones como: el servidor esta apagado, perdida repentina 
 * de conexion, o rechazo de conexion por puertos bloqueados.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class ErrorRedException extends AmongUsException {
    
    /**
     * Constructor que inicializa la excepcion de red.
     * Anade automaticamente un prefijo al mensaje para 
     * facilitar su identificacion en la consola o en cuadros de dialogo.
     * * @param detalle La descripcion exacta o motivo del fallo de conexion.
     */
    public ErrorRedException(String detalle) {
        super("ERROR DE RED: " + detalle);
    }
}