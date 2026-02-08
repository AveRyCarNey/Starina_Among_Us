package starina_among_us.vista;

import starina_among_us.modelo.Jugador;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer; // IMPORTANTE: Usar el Timer de Swing

public class PanelJuego extends JPanel implements KeyListener, ActionListener {

    private Jugador miJugador;
    private Timer reloj; // El corazón del juego
    
    // Banderas para saber qué teclas están presionadas
    private boolean arriba, abajo, izquierda, derecha;

    public PanelJuego() {
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(this);

        miJugador = new Jugador("Starina", 100, 100, false);
        
        // Configurar el reloj: Se ejecutará cada 15 milisegundos (~60 FPS)
        // "this" significa que este panel ejecutará el método actionPerformed cada vez
        reloj = new Timer(15, this);
        reloj.start(); // ¡Arrancamos el motor!
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        miJugador.dibujar(g, this);
    }

    // --- CICLO DEL JUEGO (Lo que hace el reloj cada 15ms) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        // 1. Calculamos a dónde moverse según las teclas presionadas
        int dx = 0;
        int dy = 0;
        
        if (izquierda) dx = -1;
        if (derecha)   dx = 1;
        if (arriba)    dy = -1;
        if (abajo)     dy = 1;
        
        // 2. Si hay movimiento, movemos al jugador
        if (dx != 0 || dy != 0) {
            miJugador.mover(dx, dy);
        } else {
            miJugador.detener();
        }
        
        // 3. Redibujamos la pantalla
        repaint();
    }

    // --- TECLADO (Solo enciende/apaga interruptores) ---
    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();
        
        if (tecla == KeyEvent.VK_RIGHT) derecha = true;
        if (tecla == KeyEvent.VK_LEFT)  izquierda = true;
        if (tecla == KeyEvent.VK_UP)    arriba = true;
        if (tecla == KeyEvent.VK_DOWN)  abajo = true;
        
        if (tecla == KeyEvent.VK_SPACE) miJugador.setVivo(!miJugador.isVivo());
    }

    @Override 
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();
        
        if (tecla == KeyEvent.VK_RIGHT) derecha = false;
        if (tecla == KeyEvent.VK_LEFT)  izquierda = false;
        if (tecla == KeyEvent.VK_UP)    arriba = false;
        if (tecla == KeyEvent.VK_DOWN)  abajo = false;
    }
    
    @Override public void keyTyped(KeyEvent e) {}
}