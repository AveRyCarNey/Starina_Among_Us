package starina_among_us.vista;

import javax.swing.JFrame;

public class VentanaJuego extends JFrame {

    public VentanaJuego() {
        this.setTitle("Among Us - Villa Asia Edition");
        this.setSize(800, 600); // Tamaño de la ventana
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Que se cierre al dar a la X
        this.setLocationRelativeTo(null); // Centrar en pantalla
        this.setResizable(false); // Que no le cambien el tamaño

        // Agregamos el Panel que acabamos de crear
        PanelJuego panel = new PanelJuego();
        this.add(panel);
    }
}