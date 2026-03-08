package starina_among_us.modelo.excepciones;

/**
 * Clase base para todas las excepciones personalizadas del juego Starina Among Us.
 * Hereda de la clase Exception de Java y sirve como superclase generica
 * para categorizar y capturar los errores especificos de este proyecto.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class AmongUsException extends Exception {
    
    /**
     * Constructor que inicializa la excepcion con un mensaje descriptivo.
     * * @param mensaje El texto que detalla la causa exacta por la cual se lanzo la excepcion.
     */
    public AmongUsException(String mensaje) {
        super(mensaje);
    }
}