package starina_among_us;

import starina_among_us.vista.VentanaMenu;

/**
 * Clase principal que actua como punto de entrada de la aplicacion.
 * Se encarga de arrancar el juego inicializando y mostrando el menu principal.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class Starina_Among_US {
    
    /**
     * Metodo principal (Main) que arranca la maquina virtual de Java para este proyecto.
     * Crea una instancia de la ventana del menu y la hace visible para el usuario.
     * * @param args Argumentos de la linea de comandos (no se utilizan en este proyecto).
     */
    public static void main(String[] args) {
        VentanaMenu menu = new VentanaMenu();
        menu.setVisible(true);
    }
}