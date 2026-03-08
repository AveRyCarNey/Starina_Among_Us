package starina_among_us.modelo; 

import java.awt.Rectangle;

/**
 * Representa una mision interactiva (Tarea) dentro del mapa del juego.
 * Almacena la zona de colision donde el jugador debe pararse para interactuar
 * y mantiene el estado de completitud de dicha tarea.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class Tarea {
    private String id;
    private String nombre;
    private Rectangle zonaActiva;
    private boolean completada;

    /**
     * Constructor principal de una tarea generica.
     * * @param id Identificador interno unico.
     * @param nombre Nombre visible para el usuario.
     * @param zonaActiva Rectangulo de colision que define el area de interaccion fisica en el mapa.
     */
    public Tarea(String id, String nombre, Rectangle zonaActiva) {
        this.id = id;
        this.nombre = nombre;
        this.zonaActiva = zonaActiva;
        this.completada = false;
    }

    /**
     * @return El ID unico de la tarea.
     */
    public String getId() { return id; }

    /**
     * @return El nombre descriptivo de la tarea.
     */
    public String getNombre() { return nombre; }

    /**
     * @return El rectangulo que representa el area interactuable en el mapa.
     */
    public Rectangle getZonaActiva() { return zonaActiva; }

    /**
     * @return true si la tarea ya fue realizada por el jugador, false en caso contrario.
     */
    public boolean isCompletada() { return completada; }

    /**
     * Modifica el estado actual de la tarea.
     * * @param completada true para marcarla como hecha.
     */
    public void setCompletada(boolean completada) { this.completada = completada; }
}