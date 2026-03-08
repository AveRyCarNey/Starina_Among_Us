package starina_among_us.vista;

import java.awt.Color;
import javax.swing.JFrame;
import starina_among_us.modelo.excepciones.AmongUsException;

/**
 * Contenedor principal de la ventana (Frame) donde se ejecuta la partida.
 * Su unica funcion es crear y encapsular el {@link PanelJuego}, pasandole
 * todos los parametros de configuracion seleccionados en el menu.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class VentanaJuego extends JFrame {

    /**
     * Constructor que inicializa la ventana de la partida en curso.
     * * @param mapa Nombre del mapa seleccionado ("Uni" o "Salones").
     * @param ip Direccion del servidor o palabra clave ("OFFLINE", "OFFLINE_IMPOSTOR") para modo libre.
     * @param nombre Nickname elegido por el jugador.
     * @param color Color en formato RGB elegido por el jugador.
     * @param esHost Define si esta ventana le pertenece al creador de la sala.
     * @throws AmongUsException Si ocurre algun error critico al cargar el mapa o los recursos en el PanelJuego.
     */
    public VentanaJuego(String mapa, String ip, String nombre, Color color, boolean esHost) throws AmongUsException {
        setTitle("Among Us - Villa Asia Edition");
        setSize(800, 600);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        boolean esModoLibre = ip.equals("OFFLINE");
        
        // Le pasamos todo al panel principal de renderizado
        PanelJuego panel = new PanelJuego(mapa, ip, nombre, color, esHost);
        add(panel);
        
        setLocationRelativeTo(null);
    }
}