package starina_among_us.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import starina_among_us.modelo.HerramientasImagen;

/**
 * Ventana emergente interactiva para la tarea de apagar la PC.
 * Simula una terminal de computadora donde el jugador debe ingresar
 * un codigo de acceso mediante el teclado fisico para apagar el sistema.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class VistaMisionPC extends JDialog {

    private boolean completada = false;
    private String pinIngresado = "";
    private BufferedImage imgPantalla;
    private BufferedImage imgTeclado;
    
    private boolean verificando = false;
    private String mensajePantalla = starina_among_us.modelo.GestorLenguaje.get("lbl_pc_ingrese");

    /**
     * Constructor principal del minijuego de la PC interactiva.
     * * @param padre Panel principal del juego para sincronizacion modal.
     */
    public VistaMisionPC(PanelJuego padre) {
        super(SwingUtilities.getWindowAncestor(padre), "Apagar PC", Dialog.ModalityType.APPLICATION_MODAL);
        
        setSize(700, 620);
        setLocationRelativeTo(padre);
        setUndecorated(true);

        try {
            BufferedImage imgOriginal = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/pc.png"));
            imgPantalla = HerramientasImagen.recortar(imgOriginal, 0, 0, 505, 361);
            imgTeclado = HerramientasImagen.recortar(imgOriginal, 506, 0, 597, 174);
        } catch (Exception e) {
            System.out.println("Error cargando o recortando pc.png: " + e.getMessage());
        }

        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                
                g2.setColor(new Color(20, 20, 30));
                g2.fillRect(0, 0, getWidth(), getHeight());

                int pantallaX = (getWidth() - 505) / 2;
                int pantallaY = 20;
                if (imgPantalla != null) {
                    g2.drawImage(imgPantalla, pantallaX, pantallaY, null);
                }

                int tecladoX = (getWidth() - 597) / 2;
                int tecladoY = pantallaY + 361 + 20; 
                if (imgTeclado != null) {
                    g2.drawImage(imgTeclado, tecladoX, tecladoY, null);
                }

                g2.setColor(new Color(50, 255, 50)); 
                g2.setFont(new Font("Monospaced", Font.BOLD, 22));
                g2.drawString(mensajePantalla, pantallaX + 50, pantallaY + 140);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Monospaced", Font.BOLD, 36));
                g2.drawString("> " + pinIngresado + "_", pantallaX + 50, pantallaY + 200);
            }
        };
        panelFondo.setLayout(null);
        setContentPane(panelFondo);

        panelFondo.setFocusable(true);
        panelFondo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (verificando) return; 

                int key = e.getKeyCode();
                if (key == KeyEvent.VK_BACK_SPACE && pinIngresado.length() > 0) {
                    pinIngresado = pinIngresado.substring(0, pinIngresado.length() - 1);
                    starina_among_us.modelo.GestorSonido.jugar("general_sounds/UI_Hover.wav"); 
                } 
                else if (key == KeyEvent.VK_ENTER) {
                    verificarPassword();
                }
                panelFondo.repaint();
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (verificando) return;
                char c = e.getKeyChar();
                
                if (Character.isLetterOrDigit(c)) {
                    if (pinIngresado.length() < 15) { 
                        pinIngresado += c;
                        starina_among_us.modelo.GestorSonido.jugar("general_sounds/UI_Hover.wav");
                    }
                }
                panelFondo.repaint();
            }
        });

        panelFondo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { panelFondo.requestFocusInWindow(); }
        });

        JButton btnCerrar = new JButton("X");
        btnCerrar.setBounds(630, 20, 50, 40);
        btnCerrar.setBackground(Color.RED);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        panelFondo.add(btnCerrar);

        SwingUtilities.invokeLater(() -> panelFondo.requestFocusInWindow());
    }

    /**
     * Valida la contrasena ingresada por el jugador contra el pin de seguridad.
     * Muestra retroalimentacion visual de acuerdo al resultado.
     */
    private void verificarPassword() {
        verificando = true; 
        
        if (pinIngresado.equalsIgnoreCase("Starina")) {
            mensajePantalla = starina_among_us.modelo.GestorLenguaje.get("lbl_pc_concedido");
            pinIngresado = starina_among_us.modelo.GestorLenguaje.get("lbl_pc_apagando");
            completada = true; 
            starina_among_us.modelo.GestorSonido.jugar("general_sounds/UI_Select.wav"); 
            
            Timer t = new Timer(1000, e -> dispose());
            t.setRepeats(false);
            t.start();
        } else {
            mensajePantalla = starina_among_us.modelo.GestorLenguaje.get("lbl_pc_denegado");
            pinIngresado = "";
            starina_among_us.modelo.GestorSonido.jugar("general_sounds/Alarm_sabotage.wav"); 
            
            Timer t = new Timer(1000, e -> {
                mensajePantalla = starina_among_us.modelo.GestorLenguaje.get("lbl_pc_ingrese");
                verificando = false; 
                repaint();
            });
            t.setRepeats(false);
            t.start();
        }
    }

    /**
     * @return El estado de resolucion de la mision.
     */
    public boolean isCompletada() { return completada; }
}