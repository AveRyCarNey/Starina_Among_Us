package starina_among_us.vista;

import java.awt.Color;
import javax.swing.JFrame;

public class VentanaJuego extends JFrame {

    public VentanaJuego(String mapa, String ip, String nombre, Color color, boolean esHost) {
        setTitle("Among Us - Villa Asia Edition");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Le pasamos todo al panel
        PanelJuego panel = new PanelJuego(mapa, ip, nombre, color, esHost);
        add(panel);
        
        setLocationRelativeTo(null);
    }
}