package starina_among_us.modelo.excepciones;

/**
 * Excepcion personalizada que se lanza cuando el motor grafico o de sonido 
 * del juego no puede encontrar o cargar un archivo local indispensable 
 * (imagenes PNG/JPG, sonidos WAV, o mapas de datos de colision).
 * Previene que el juego colapse silenciosamente si falta un recurso.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class RecursoNoEncontradoException extends AmongUsException {
    
    /**
     * Constructor que inicializa la excepcion de recurso faltante.
     * Formatea el mensaje para mostrar claramente el nombre del archivo defectuoso.
     * * @param recurso El nombre, ruta o identificador del archivo que fallo al cargar.
     */
    public RecursoNoEncontradoException(String recurso) {
        super("ERROR DE RECURSO: No se pudo cargar '" + recurso + "'. Verifica la ruta.");
    }
}