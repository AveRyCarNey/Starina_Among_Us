/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package starina_among_us.modelo; // Ajusta el paquete si es necesario

import java.awt.Rectangle;

public class Tarea {
    private String id;
    private String nombre;
    private Rectangle zonaActiva;
    private boolean completada;

    public Tarea(String id, String nombre, Rectangle zonaActiva) {
        this.id = id;
        this.nombre = nombre;
        this.zonaActiva = zonaActiva;
        this.completada = false;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public Rectangle getZonaActiva() { return zonaActiva; }
    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
}
